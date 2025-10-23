package A3.AnhembiMorumBank.DTO.ContaDTO;

import A3.AnhembiMorumBank.model.Conta;

import java.math.BigDecimal;

public record SaldoDTO(
        String nome,
        BigDecimal saldo
) {
    public SaldoDTO(Conta saldo){
        this(saldo.getCliente().getNome(), saldo.getSaldo());
    }
}
