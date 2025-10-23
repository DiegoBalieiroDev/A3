package A3.AnhembiMorumBank.DTO.Cliente;

import A3.AnhembiMorumBank.model.Cliente;
import A3.AnhembiMorumBank.model.TipoCliente;

import java.math.BigDecimal;

public record ClienteListagemDTO(
        Long id,
        String nome,
        String telefone,
        String email,
        String cpf,
        TipoCliente tipoCliente,
        String chavePix,
        String agencia,
        String numeroConta,
        BigDecimal saldo
) {

    public ClienteListagemDTO(Cliente cliente) {
        this(   cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getCpf(),
                cliente.getTipoCliente(),
                cliente.getChavePix(),
                cliente.getConta().getAgencia(),
                cliente.getConta() != null ? cliente.getConta().getNumeroConta() : null,
                cliente.getConta() != null ? cliente.getConta().getSaldo() : null);
    }
}
