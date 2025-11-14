package A3.AnhembiMorumBank.Repository;

import A3.AnhembiMorumBank.DTO.Transacao.TransacaoResponseDTO;
import A3.AnhembiMorumBank.model.Cliente;
import A3.AnhembiMorumBank.model.StatusTransacao;
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
    @Query("select coalesce(sum(t.valor), 0) from Transacao t where t.clienteOrigem = :cliente and t.data >= :from")
    BigDecimal findTotalValorByClienteAndDataAfter(Cliente cliente, LocalDateTime from);

    List<Transacao> findByClienteOrigemAndDataAfter(Cliente clienteOrigem, LocalDateTime from);

    @Query("select count(t) from Transacao t where t.clienteOrigem = :cliente and t.data >= :from")
    long countByClienteOrigemAndDataAfter(Cliente cliente, LocalDateTime from);

    @Query("select avg(t.valor) from Transacao t where t.clienteOrigem = :cliente")
    BigDecimal findAverageValorByCliente(Cliente cliente);

    // conta quantas transacoes do mesmo valor foram feitas por cliente após 'from'
    @Query("select count(t) from Transacao t where t.clienteOrigem = :cliente and t.valor = :valor and t.data >= :from")
    long countByClienteOrigemAndValorAndDataAfter(Cliente cliente, BigDecimal valor, LocalDateTime from);

    List<Transacao> findByClienteOrigemOrCpfDestinatario(Cliente origem, String cpfDestinatario);


    List<Transacao> findByStatus(StatusTransacao statusTransacao);

}
