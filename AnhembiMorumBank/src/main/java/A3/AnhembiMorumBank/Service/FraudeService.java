package A3.AnhembiMorumBank.Service;

import A3.AnhembiMorumBank.DTO.Transacao.TransacaoDTO;
import A3.AnhembiMorumBank.Repository.TransacaoRepository;
import A3.AnhembiMorumBank.model.Cliente;
import A3.AnhembiMorumBank.model.Conta;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FraudeService {

    private final TransacaoRepository transacaoRepository;

    public FraudeService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    public FraudResult evaluate(TransacaoDTO dto, Cliente clienteOrigem, Conta contaOrigem, Cliente clienteDestino, Conta contaDestino
    ) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        // 1) Regra: valor muito alto
        if (dto.valor() != null && dto.valor().compareTo(new BigDecimal("3000")) > 0) {
            score += 60;
            reasons.add("Valor maior que 3000");
        }

        // 2) Limite diário
        BigDecimal limiteDiario = new BigDecimal("10000");
        BigDecimal totalHoje = transacaoRepository
                .findTotalValorByClienteAndDataAfter(clienteOrigem, LocalDate.now().atStartOfDay());
        if (totalHoje == null) totalHoje = BigDecimal.ZERO;
        if (dto.valor() != null && totalHoje.add(dto.valor()).compareTo(limiteDiario) > 0) {
            score += 60;
            reasons.add("Ultrapassa limite diário");
        }

        // 3) Horario noturno
        int hora = LocalDateTime.now().getHour();
        if (hora >= 22 || hora < 6) {
            score += 20;
            reasons.add("Transação em horário noturno");
        }

        // 4) Muitas transações no último minuto
        var since = LocalDateTime.now().minusMinutes(1);
        List<?> txsRec = transacaoRepository.findByClienteOrigemAndDataAfter(clienteOrigem, since);
        if (txsRec != null && txsRec.size() >= 3) {
            score += 40;
            reasons.add("Muitas transações no último minuto");
        }

        // 5) Número alto de transações hoje
        long countHoje = transacaoRepository.countByClienteOrigemAndDataAfter(
                clienteOrigem,
                LocalDate.now().atStartOfDay()
        );
        if (countHoje > 20) {
            score += 30;
            reasons.add("Número alto de transações hoje");
        }

        // ✅ 6) Conta destino nova (<7 dias)
        if (contaDestino != null &&
                contaDestino.getCriacao() != null &&
                contaDestino.getCriacao().isAfter(LocalDate.now().minusDays(7).atStartOfDay())) {

            score += 20;
            reasons.add("Conta destino criada recentemente");
        }

        // 7) Valor acima da média
        BigDecimal media = transacaoRepository.findAverageValorByCliente(clienteOrigem);
        if (media == null) media = BigDecimal.ZERO;
        if (media.compareTo(BigDecimal.ZERO) > 0 && dto.valor() != null &&
                dto.valor().compareTo(media.multiply(new BigDecimal("5"))) > 0) {
            score += 30;
            reasons.add("Valor muito acima da média habitual");
        }

        // 8) Mesmo valor repetido
        if (dto.valor() != null) {
            LocalDateTime sinceSameAmount = LocalDateTime.now().minusMinutes(10);
            long sameAmountCount =
                    transacaoRepository.countByClienteOrigemAndValorAndDataAfter(
                            clienteOrigem,
                            dto.valor(),
                            sinceSameAmount
                    );
            if (sameAmountCount >= 1) {
                score += 40;
                reasons.add("Mesmo valor executado várias vezes recentemente");
            }
        }

        // Thresholds
        if (score >= 80) return new FraudResult(score, reasons, FraudResult.Action.DENY);
        if (score >= 40) return new FraudResult(score, reasons, FraudResult.Action.PENDING_REVIEW);

        return new FraudResult(score, reasons, FraudResult.Action.APPROVE);
    }
}