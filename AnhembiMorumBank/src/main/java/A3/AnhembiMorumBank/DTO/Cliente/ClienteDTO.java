package A3.AnhembiMorumBank.DTO.Cliente;

import A3.AnhembiMorumBank.DTO.Endereco.EnderecoDTO;

import A3.AnhembiMorumBank.model.TipoCliente;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDateTime;

public record ClienteDTO(

        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String telefone,

        @NotBlank
        @CPF
        String cpf,

        Boolean ativo,

        LocalDateTime dataCadastro,

        @NotNull
        TipoCliente tipoCliente,

        @NotNull
        EnderecoDTO endereco,

        String senha,

        @NotBlank
        @Pattern(regexp = "\\d{4}", message = "PIN deve conter exatamente 4 dígitos.")
        String pin
) {
}
