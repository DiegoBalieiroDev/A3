package A3.AnhembiMorumBank.DTO.Transacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransacaoDTO(
        @NotNull
        Long clienteOrigemId,

        @NotNull
        BigDecimal valor,

        @NotBlank
        String chavePixDestino,


        String nomeDestinatario,


        String cpfDestinatario


) {
}
