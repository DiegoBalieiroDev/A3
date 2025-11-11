package A3.AnhembiMorumBank.DTO.ContaDTO;

import A3.AnhembiMorumBank.model.Conta;

import java.math.BigDecimal;

public record ContaDTO(
        Long id,
        String numeroConta,
        String agencia,
        BigDecimal saldo
) {
    public ContaDTO(Conta conta) {
        this(conta.getId(), conta.getNumeroConta(), conta.getAgencia(), conta.getSaldo());
    }
}
