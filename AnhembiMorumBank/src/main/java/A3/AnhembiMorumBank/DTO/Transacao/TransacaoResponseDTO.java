package A3.AnhembiMorumBank.DTO.Transacao;

import A3.AnhembiMorumBank.model.Transacao;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponseDTO(
        Long id,
        String nomeOrigem,
        String cpfOrigem,
        String nomeDestinatario,
        String cpfDestinatario,
        BigDecimal valor,
        String status,
        boolean suspeitaGolpe,

        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
        LocalDateTime data,

        Integer fraudScore,
        String fraudReasons
) {
    public TransacaoResponseDTO(Transacao transacao) {
        this(
                transacao.getId(),
                transacao.getClienteOrigem().getNome(),
                transacao.getClienteOrigem().getCpf(),
                transacao.getNomeDestinatario(),
                transacao.getCpfDestinatario(),
                transacao.getValor(),
                transacao.getStatus().toString(),
                transacao.isSuspeitaGolpe(),
                transacao.getData(),
                transacao.getFraudScore(),
                transacao.getFraudReasons()
        );
    }
}