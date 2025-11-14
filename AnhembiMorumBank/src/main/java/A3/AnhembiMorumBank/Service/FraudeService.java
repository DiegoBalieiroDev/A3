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

    public FraudResult evaluate(TransacaoDTO dto, Cliente clienteOrigem, Conta contaOrigem) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        // 1) Regra: valor muito alto
        if (dto.valor() != null && dto.valor().compareTo(new BigDecimal("5000")) > 0) {
            score += 60;
            reasons.add("Valor maior que 5000");
        }

        // 2) Regra: limite diário (soma dos valores hoje)
        BigDecimal limiteDiario = new BigDecimal("10000");
        BigDecimal totalHoje = transacaoRepository
                .findTotalValorByClienteAndDataAfter(clienteOrigem, LocalDate.now().atStartOfDay());
        if (totalHoje == null) totalHoje = BigDecimal.ZERO;
        if (dto.valor() != null && totalHoje.add(dto.valor()).compareTo(limiteDiario) > 0) {
            score += 60;
            reasons.add("Ultrapassa limite diário");
        }

        // 3) Regra: horário (noturno)
        int hora = LocalDateTime.now().getHour();
        if (hora >= 22 || hora < 6) {
            score += 20;
            reasons.add("Transação em horário noturno");
        }

        // 4) Regra: muitas transações em curto período (velocidade / smurfing)
        var since = LocalDateTime.now().minusMinutes(1);
        List<?> txsRec = transacaoRepository.findByClienteOrigemAndDataAfter(clienteOrigem, since);
        if (txsRec != null && txsRec.size() >= 3) {
            score += 40;
            reasons.add("Muitas transações no último minuto");
        }

        // 5) Regra: número de transações diárias (contagem)
        long countHoje = transacaoRepository.countByClienteOrigemAndDataAfter(clienteOrigem, LocalDate.now().atStartOfDay());
        if (countHoje > 20) {
            score += 30;
            reasons.add("Número alto de transações hoje");
        }

        // 6) Regra: conta nova (<7 dias)
        if (contaOrigem.getCriacao() != null && contaOrigem.getCriacao().isAfter(LocalDate.now().minusDays(7).atStartOfDay())) {
            score += 20;
            reasons.add("Conta criada recentemente");
        }

        // 7) Regra: valor muito acima da média do cliente (heurística)
        BigDecimal media = transacaoRepository.findAverageValorByCliente(clienteOrigem);
        if (media == null) media = BigDecimal.ZERO;
        if (media.compareTo(BigDecimal.ZERO) > 0 && dto.valor() != null &&
                dto.valor().compareTo(media.multiply(new BigDecimal("5"))) > 0) {
            score += 30;
            reasons.add("Valor muito acima da média habitual");
        }

        // Decisão por thresholds (exemplos)
        if (score >= 80) {
            return new FraudResult(score, reasons, FraudResult.Action.DENY);
        } else if (score >= 40) {
            return new FraudResult(score, reasons, FraudResult.Action.PENDING_REVIEW);
        } else {
            return new FraudResult(score, reasons, FraudResult.Action.APPROVE);
        }
    }
}