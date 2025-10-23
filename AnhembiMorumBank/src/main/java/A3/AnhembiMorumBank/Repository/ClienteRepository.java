package A3.AnhembiMorumBank.Repository;

import A3.AnhembiMorumBank.model.Cliente;
import A3.AnhembiMorumBank.model.TipoCliente;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Page<Cliente> findAllByAtivoTrue(Pageable paginacao);

    Page<Cliente> findAllByAtivoTrueAndTipoCliente(TipoCliente tipo, Pageable paginacao);

    Optional<Cliente> findByChavePix(String chavePix);

    boolean existsByConta_NumeroConta(String numero);
}
