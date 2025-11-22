package A3.AnhembiMorumBank.DTO.Cliente;

import A3.AnhembiMorumBank.DTO.ContaDTO.ContaDTO;
import A3.AnhembiMorumBank.model.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DetalhamentoClienteDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String cpf,
        String chavePix,
        String numeroConta,
        String agencia,
        Boolean ativo,
        TipoCliente tipoCliente,
        BigDecimal saldo,
        Endereco endereco,
        LocalDateTime dataCadastro,
        ContaDTO conta
) {
    public DetalhamentoClienteDTO(Cliente cliente) {
        this(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getTelefone(), cliente.getCpf(), cliente.getChavePix(),cliente.getConta().getNumeroConta(), cliente.getConta().getAgencia(), cliente.getAtivo(), cliente.getTipoCliente(), cliente.getConta().getSaldo(), cliente.getEndereco(), cliente.getDataCadastro(), new ContaDTO(cliente.getConta()));
    }
}
