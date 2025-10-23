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

@Service
public class TransacaoService {

    @Autowired
    TransacaoRepository transacaoRepository;

    @Autowired
    ContaRepository contaRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Transactional
    public Transacao realizarTransacao(TransacaoDTO transacaoDTO) {

        var clienteOrigem = clienteRepository.findById(transacaoDTO.clienteOrigemId())
                .orElseThrow(() -> new RuntimeException("Cliente de origem não encontrado."));

        var contaOrigem = contaRepository.findByCliente(clienteOrigem)
                .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada."));

        // validacoes
        if (transacaoDTO.valor() == null || transacaoDTO.valor().compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("Valor inválido para transação.");

        if (transacaoDTO.chavePixDestino() == null || transacaoDTO.chavePixDestino().isBlank())
            throw new RuntimeException("Chave PIX destino é obrigatória.");

        if (clienteOrigem.getChavePix().equals(transacaoDTO.chavePixDestino()))
            throw new RuntimeException("Não é permitido transferir para si mesmo.");

        // transacao base
        var transacao = new Transacao(clienteOrigem, transacaoDTO);
        transacao.setData(LocalDateTime.now());

        // anti fraude

        boolean negada = false;

        if (transacaoDTO.valor().compareTo(new BigDecimal("5000")) > 0) {
            transacao.setStatus(StatusTransacao.NEGADA);
            transacao.setSuspeitaGolpe(true);
            negada = true;


        }

        BigDecimal limiteDiario = new BigDecimal("10000");
        BigDecimal totalHoje = transacaoRepository
                .findTotalValorByClienteAndDataAfter(clienteOrigem, LocalDate.now().atStartOfDay());

        if (totalHoje.add(transacaoDTO.valor()).compareTo(limiteDiario) > 0) {
            transacao.setStatus(StatusTransacao.NEGADA);
            transacao.setSuspeitaGolpe(true);
            negada = true;
        }

        int hora = LocalDateTime.now().getHour();
        if (hora >= 22 || hora < 6) {
            transacao.setStatus(StatusTransacao.NEGADA);
            transacao.setSuspeitaGolpe(true);
            negada = true;
        }

        var transacoesRecentes = LocalDateTime.now().minusMinutes(1);
        List<Transacao> transacoes = transacaoRepository.findByClienteOrigemAndDataAfter(clienteOrigem, transacoesRecentes);
        if (transacoes.size() >= 3) {
            transacao.setStatus(StatusTransacao.NEGADA);
            transacao.setSuspeitaGolpe(true);
            negada = true;
        }

        if (negada) {
            if (transacao.getNomeDestinatario() == null) {
                transacao.setNomeDestinatario("DESTINATÁRIO DESCONHECIDO");
            }
            if (transacao.getCpfDestinatario() == null) {
                transacao.setCpfDestinatario("00000000000");
            }
            return transacaoRepository.save(transacao);
        }



        // localiza cliente de destino
        var clienteDestinoOpt = clienteRepository.findByChavePix(transacaoDTO.chavePixDestino());

        if (clienteDestinoOpt.isPresent()) {


            // PIX INTERNO
            var clienteDestino = clienteDestinoOpt.get();
            var contaDestino = contaRepository.findByCliente(clienteDestino)
                    .orElseThrow(() -> new RuntimeException("Conta de destino não encontrada."));

            if (contaOrigem.getSaldo().compareTo(transacaoDTO.valor()) < 0)
                throw new RuntimeException("Saldo insuficiente.");


            contaOrigem.sacar(transacaoDTO.valor());
            contaDestino.depositar(transacaoDTO.valor());


            transacao.setNomeDestinatario(clienteDestino.getNome());
            transacao.setCpfDestinatario(clienteDestino.getCpf());
            transacao.setStatus(StatusTransacao.APROVADA);
            transacao.setSuspeitaGolpe(false);

            contaRepository.save(contaOrigem);
            contaRepository.save(contaDestino);
        } else {
            if (contaOrigem.getSaldo().compareTo(transacaoDTO.valor()) < 0)
                throw new RuntimeException("Saldo insuficiente.");

            contaOrigem.sacar(transacaoDTO.valor());
            contaRepository.save(contaOrigem);

            transacao.setNomeDestinatario("PIX externo");
            transacao.setCpfDestinatario("00000000000");
            transacao.setStatus(StatusTransacao.APROVADA);
            transacao.setSuspeitaGolpe(false);
        }

        return transacaoRepository.save(transacao);
    }

//    public List<ListagemTransacaoDTO> converteDados(List<Transacao> transacoes) {
//        return transacoes.stream()
//                .map(t -> new ListagemTransacaoDTO(
//                        t.getId(),
//                        t.getValor(),
//                        t.getChavePixDestino(),
//                        t.getCpfDestinatario(),
//                        t.getData(),
//                        t.getChavePix(),
//                        t.isSuspeitaGolpe()
//                ))
//                .collect(Collectors.toList());
//    }
//
//    public Page<ListagemTransacaoDTO> listarTransacoesCliente(Pageable paginacao) {
//        return transacaoRepository.findAllByClienteOrigem(paginacao)
//                .map(ListagemTransacaoDTO::new);
//    }




}
