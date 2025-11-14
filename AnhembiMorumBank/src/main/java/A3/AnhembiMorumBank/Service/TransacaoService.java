package A3.AnhembiMorumBank.Service;

import A3.AnhembiMorumBank.DTO.Transacao.TransacaoDTO;
import A3.AnhembiMorumBank.DTO.Transacao.TransacaoResponseDTO;
import A3.AnhembiMorumBank.Repository.ClienteRepository;
import A3.AnhembiMorumBank.Repository.ContaRepository;
import A3.AnhembiMorumBank.Repository.TransacaoRepository;
import A3.AnhembiMorumBank.model.StatusTransacao;
import A3.AnhembiMorumBank.model.Transacao;
import A3.AnhembiMorumBank.model.Cliente;
import A3.AnhembiMorumBank.model.Conta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {

    private static final Logger log = LoggerFactory.getLogger(TransacaoService.class);

    @Autowired private TransacaoRepository transacaoRepository;
    @Autowired private ContaRepository contaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private FraudeService fraudeService;

    @Transactional
    public Transacao realizarTransacao(TransacaoDTO transacaoDTO) {

        // carregar origem
        Cliente clienteOrigem = clienteRepository.findById(transacaoDTO.clienteOrigemId())
                .orElseThrow(() -> new RuntimeException("Cliente de origem não encontrado."));

        Conta contaOrigem = contaRepository.findByCliente(clienteOrigem)
                .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada."));

        if (transacaoDTO.valor() == null || transacaoDTO.valor().compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("Valor inválido para transação.");
        if (transacaoDTO.chavePixDestino() == null || transacaoDTO.chavePixDestino().isBlank())
            throw new RuntimeException("Chave PIX destino é obrigatória.");
        if (clienteOrigem.getChavePix() != null &&
                clienteOrigem.getChavePix().equals(transacaoDTO.chavePixDestino()))
            throw new RuntimeException("Não é permitido transferir para si mesmo.");

        // criar transacao em memória
        Transacao transacao = new Transacao(clienteOrigem, transacaoDTO);
        transacao.setData(LocalDateTime.now());

        // destino da transacao
        Optional<Cliente> clienteDestinoOpt = clienteRepository.findByChavePix(transacaoDTO.chavePixDestino());
        Cliente clienteDestino = null;
        Conta contaDestino = null;

        if (clienteDestinoOpt.isPresent()) {

            clienteDestino = clienteDestinoOpt.get();
            transacao.setNomeDestinatario(clienteDestino.getNome());
            transacao.setCpfDestinatario(clienteDestino.getCpf());

            contaDestino = contaRepository.findByCliente(clienteDestino)
                    .orElse(null);

            log.debug("Destinatário interno: id={}, nome={}, cpf={}",
                    clienteDestino.getId(), clienteDestino.getNome(), clienteDestino.getCpf());

        } else {
            // se cliente e conta forem null
            transacao.setNomeDestinatario("PIX externo");
            transacao.setCpfDestinatario("00000000000");

            log.debug("Destinatário PIX externo: chave={}", transacaoDTO.chavePixDestino());
        }

        // 4) FRAUDE
        FraudResult resultado = fraudeService.evaluate(
                transacaoDTO,
                clienteOrigem,
                contaOrigem,
                clienteDestino,
                contaDestino
        );

        transacao.setFraudScore(resultado.getScore());
        transacao.setFraudReasons(String.join("; ", resultado.getReasons()));

        log.debug("Fraude resultado: score={}, action={}, reasons={}",
                resultado.getScore(), resultado.getAction(), resultado.getReasons());

        // 5) DENY
        if (resultado.getAction() == FraudResult.Action.DENY) {
            transacao.setStatus(StatusTransacao.NEGADA);
            transacao.setSuspeitaGolpe(true);

            return transacaoRepository.save(transacao);
        }

        // 6) PENDENTE
        if (resultado.getAction() == FraudResult.Action.PENDING_REVIEW) {

            transacao.setStatus(StatusTransacao.PENDENTE_ANALISE);
            transacao.setSuspeitaGolpe(true);

            return transacaoRepository.save(transacao);
        }

        // 7) APROVAR — movimentação financeira
        if (contaOrigem.getSaldo().compareTo(transacaoDTO.valor()) < 0)
            throw new RuntimeException("Saldo insuficiente.");

        if (clienteDestino != null) {

            if (contaDestino == null)
                throw new RuntimeException("Conta destino não encontrada.");

            contaOrigem.sacar(transacaoDTO.valor());
            contaDestino.depositar(transacaoDTO.valor());

            contaRepository.save(contaOrigem);
            contaRepository.save(contaDestino);

            log.debug("Transferência interna: {} -> {} valor={}",
                    clienteOrigem.getId(), clienteDestino.getId(), transacaoDTO.valor());

        } else {
            // PIX externo: só debita
            contaOrigem.sacar(transacaoDTO.valor());
            contaRepository.save(contaOrigem);

            log.debug("Transferência PIX externo: origem={} valor={}",
                    clienteOrigem.getId(), transacaoDTO.valor());
        }

        transacao.setStatus(StatusTransacao.APROVADA);
        transacao.setSuspeitaGolpe(false);

        return transacaoRepository.save(transacao);
    }

    public List<Transacao> listarTransacoesDoCliente(String login) {

        Cliente cliente = clienteRepository.findByEmail(login)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        return transacaoRepository.findByClienteOrigemOrCpfDestinatario(
                cliente,
                cliente.getCpf()
        );
    }

    public Optional<Transacao> buscarPorId(Long id) {
        return transacaoRepository.findById(id);
    }

    public List<Transacao> listarTodas() {
        return transacaoRepository.findAll();
    }


    // admin

    public void aprovarTransacao(Long id) {
        Transacao t = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não existe"));

        if (t.getStatus() != StatusTransacao.PENDENTE_ANALISE)
            throw new RuntimeException("Transação não está pendente");

        t.setStatus(StatusTransacao.APROVADA);
        t.setSuspeitaGolpe(false);

        transacaoRepository.save(t);
    }

    public void negarTransacao(Long id) {
        Transacao t = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não existe"));

        if (t.getStatus() != StatusTransacao.PENDENTE_ANALISE)
            throw new RuntimeException("Transação não está pendente");

        t.setStatus(StatusTransacao.NEGADA);
        t.setSuspeitaGolpe(true);

        transacaoRepository.save(t);
    }

    public List<Transacao> listarPendentes() {
        return transacaoRepository.findByStatus(StatusTransacao.PENDENTE_ANALISE);
    }

}