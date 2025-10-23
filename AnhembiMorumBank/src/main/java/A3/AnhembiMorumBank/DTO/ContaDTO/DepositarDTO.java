package A3.AnhembiMorumBank.DTO.ContaDTO;

import A3.AnhembiMorumBank.model.Conta;

import java.math.BigDecimal;

public record DepositarDTO(
        Long contaId,
        String nomeCliente,
        BigDecimal valorDepositado,
        BigDecimal novoSaldo
) {
    public DepositarDTO(Conta conta, BigDecimal valorDepositado) {
        this(
                conta.getId(),
                conta.getCliente().getNome(),
                valorDepositado,
                conta.getSaldo()
        );
    }
}
