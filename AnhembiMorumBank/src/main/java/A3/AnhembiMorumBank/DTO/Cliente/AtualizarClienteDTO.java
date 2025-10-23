package A3.AnhembiMorumBank.DTO.Cliente;

import A3.AnhembiMorumBank.model.Endereco;
import jakarta.validation.constraints.NotNull;

public record AtualizarClienteDTO(

        @NotNull
        Long id,

        String nome,

        String telefone,

        Endereco endereco
) {
}
