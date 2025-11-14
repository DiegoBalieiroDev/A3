package A3.AnhembiMorumBank.DTO.Transacao;

import A3.AnhembiMorumBank.model.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponseDTO(
        Long id,
        String nomeOrigem,
        String nomeDestinatario,
        String cpfDestinatario,
        BigDecimal valor,
        String status,
        boolean suspeitaGolpe,
        LocalDateTime data,
        Integer fraudScore,
        String fraudReasons
) {
    public TransacaoResponseDTO(Transacao transacao) {
        this(
                transacao.getId(),
                transacao.getClienteOrigem().getNome(),
                transacao.getNomeDestinatario(),
                transacao.getCpfDestinatario(),
                transacao.getValor(),
                transacao.getStatus().toString(),
                transacao.isSuspeitaGolpe(),
                transacao.getData(),
                transacao.getFraudScore(),      // novos campos
                transacao.getFraudReasons()
        );
    }
}