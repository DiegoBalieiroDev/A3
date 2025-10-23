package A3.AnhembiMorumBank.Repository;

import A3.AnhembiMorumBank.model.Cliente;
import A3.AnhembiMorumBank.model.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransacaoRepository extends JpaRepository <Transacao,Long> {
    Page<Transacao> findAllByClienteOrigem(Cliente cliente, Pageable pageable);

    List<Transacao> findByClienteOrigemAndDataAfter(Cliente cliente, LocalDateTime transacoesRecentes);

    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t WHERE t.clienteOrigem = :cliente AND t.data >= :data")
    BigDecimal findTotalValorByClienteAndDataAfter(@Param("cliente") Cliente cliente,
                                                   @Param("data") LocalDateTime data);
}
