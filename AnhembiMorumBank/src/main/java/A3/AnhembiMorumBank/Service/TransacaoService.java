package A3.AnhembiMorumBank.Service;

import A3.AnhembiMorumBank.DTO.Transacao.TransacaoDTO;
import A3.AnhembiMorumBank.Repository.ClienteRepository;
import A3.AnhembiMorumBank.Repository.ContaRepository;
import A3.AnhembiMorumBank.Repository.TransacaoRepository;
import A3.AnhembiMorumBank.model.StatusTransacao;
import A3.AnhembiMorumBank.model.Transacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {

    @Autowired
    TransacaoRepository transacaoRepository;
    @Autowired
    ContaRepository contaRepository;
    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    FraudeService fraudeService;

    @Transactional
    public Transacao realizarTransacao(TransacaoDTO transacaoDTO) {
        var clienteOrigem = clienteRepository.findById(transacaoDTO.clienteOrigemId())
                .orElseThrow(() -> new RuntimeException("Cliente de origem não encontrado."));
        var contaOrigem = contaRepository.findByCliente(clienteOrigem)
                .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada."));

        // validações básicas
        if (transacaoDTO.valor() == null || transacaoDTO.valor().compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("Valor inválido para transação.");
        if (transacaoDTO.chavePixDestino() == null || transacaoDTO.chavePixDestino().isBlank())
            throw new RuntimeException("Chave PIX destino é obrigatória.");
        if (clienteOrigem.getChavePix().equals(transacaoDTO.chavePixDestino()))
            throw new RuntimeException("Não é permitido transferir para si mesmo.");

        // nova transacao base
        var transacao = new Transacao(clienteOrigem, transacaoDTO);
        transacao.setData(LocalDateTime.now());

        // chama serviço de fraude
        FraudResult resultado = fraudeService.evaluate(transacaoDTO, clienteOrigem, contaOrigem);
        transacao.setFraudScore(resultado.getScore());
        transacao.setFraudReasons(String.join("; ", resultado.getReasons()));

        if (resultado.getAction() == FraudResult.Action.DENY) {
            transacao.setStatus(StatusTransacao.NEGADA);
            transacao.setSuspeitaGolpe(true);
            if (transacao.getNomeDestinatario() == null) transacao.setNomeDestinatario("DESTINATÁRIO DESCONHECIDO");
            if (transacao.getCpfDestinatario() == null) transacao.setCpfDestinatario("00000000000");
            return transacaoRepository.save(transacao);
        } else if (resultado.getAction() == FraudResult.Action.PENDING_REVIEW) {
            transacao.setStatus(StatusTransacao.PENDENTE_ANALISE); // novo status
            transacao.setSuspeitaGolpe(true);
            // aqui: enviar para fila de análise manual / notificar time de fraude / enviar OTP
            return transacaoRepository.save(transacao);
        }

        // APPROVE: movimenta saldos com lock/atomicidade (recomendo SELECT FOR UPDATE no repository)
        if (contaOrigem.getSaldo().compareTo(transacaoDTO.valor()) < 0)
            throw new RuntimeException("Saldo insuficiente.");

        // localiza cliente destino (PIX interno)
        var clienteDestinoOpt = clienteRepository.findByChavePix(transacaoDTO.chavePixDestino());
        if (clienteDestinoOpt.isPresent()) {
            var clienteDestino = clienteDestinoOpt.get();
            var contaDestino = contaRepository.findByCliente(clienteDestino)
                    .orElseThrow(() -> new RuntimeException("Conta de destino não encontrada."));

            // cheque final de saldo e movimentação
            contaOrigem.sacar(transacaoDTO.valor());
            contaDestino.depositar(transacaoDTO.valor());
            transacao.setNomeDestinatario(clienteDestino.getNome());
            transacao.setCpfDestinatario(clienteDestino.getCpf());
            transacao.setStatus(StatusTransacao.APROVADA);
            transacao.setSuspeitaGolpe(false);
            contaRepository.save(contaOrigem);
            contaRepository.save(contaDestino);
        } else {
            contaOrigem.sacar(transacaoDTO.valor());
            contaRepository.save(contaOrigem);
            transacao.setNomeDestinatario("PIX externo");
            transacao.setCpfDestinatario("00000000000");
            transacao.setStatus(StatusTransacao.APROVADA);
            transacao.setSuspeitaGolpe(false);
        }
        return transacaoRepository.save(transacao);
    }

    public Optional<Transacao> buscarPorId(Long id) {
        return transacaoRepository.findById(id);
    }

    public List<Transacao> listarTodas() {
        return transacaoRepository.findAll();
    }
}