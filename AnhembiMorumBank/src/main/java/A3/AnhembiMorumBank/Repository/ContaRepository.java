package A3.AnhembiMorumBank.Repository;

import A3.AnhembiMorumBank.model.Cliente;
import A3.AnhembiMorumBank.model.Conta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ContaRepository  extends JpaRepository<Conta, Long> {
//    Optional<Conta> findByCliente(Cliente cliente);
    boolean existsByNumeroConta(String numeroConta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Conta> findByCliente(Cliente cliente);
}
