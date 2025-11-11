package A3.AnhembiMorumBank.DTO.Cliente;

import A3.AnhembiMorumBank.DTO.Endereco.EnderecoDTO;

import A3.AnhembiMorumBank.model.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

        String senha
) {
}
