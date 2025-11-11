package A3.AnhembiMorumBank.DTO.Cliente;

import A3.AnhembiMorumBank.model.Cliente;

public record ClientePixDTO(
        Long id,
        String nome,
        String cpf,
        String chavePix
) {

    public ClientePixDTO(Cliente cliente) {
        this(cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getChavePix());
    }
}
