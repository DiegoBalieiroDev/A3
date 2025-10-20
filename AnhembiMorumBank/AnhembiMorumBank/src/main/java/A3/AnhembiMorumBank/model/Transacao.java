package A3.AnhembiMorumBank.model;

import A3.AnhembiMorumBank.DTO.Transacao.TransacaoDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private BigDecimal valor;

    private String chavePix;

    @Column(name = "data_transacao")
    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
    private LocalDateTime data;

    @Enumerated(EnumType.STRING)
    private StatusTransacao status;

    private boolean suspeitaGolpe;

    @ManyToOne
    @JoinColumn(name = "cliente_origem_id") // foreign key
    @JsonBackReference(value = "cliente-transacoes")
    private Cliente clienteOrigem;

    private String nomeDestinatario;

    private String chavePixDestino;

    private String cpfDestinatario;

    public Transacao() {
    }

    public Transacao(Long id, BigDecimal valor, String chavePix, LocalDateTime data, StatusTransacao status, boolean suspeitaGolpe, Cliente clienteOrigem, String nomeDestinatario, String chavePixDestino, String cpfDestinatario) {
        this.id = id;
        this.valor = valor;
        this.chavePix = chavePix;
        this.data = data;
        this.status = status;
        this.suspeitaGolpe = suspeitaGolpe;
        this.clienteOrigem = clienteOrigem;
        this.nomeDestinatario = nomeDestinatario;
        this.chavePixDestino = chavePixDestino;
        this.cpfDestinatario = cpfDestinatario;
    }

    public Transacao(Cliente clienteOrigem, TransacaoDTO dto) {
        this.clienteOrigem = clienteOrigem;
        this.valor = dto.valor();
        this.chavePixDestino = dto.chavePixDestino();
        this.cpfDestinatario = dto.cpfDestinatario();
        this.data = LocalDateTime.now();
        this.status = StatusTransacao.APROVADA;
        this.suspeitaGolpe = false;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public StatusTransacao getStatus() {
        return status;
    }

    public void setStatus(StatusTransacao status) {
        this.status = status;
    }

    public boolean isSuspeitaGolpe() {
        return suspeitaGolpe;
    }

    public void setSuspeitaGolpe(boolean suspeitaGolpe) {
        this.suspeitaGolpe = suspeitaGolpe;
    }

    public Cliente getClienteOrigem() {
        return clienteOrigem;
    }

    public void setClienteOrigem(Cliente clienteOrigem) {
        this.clienteOrigem = clienteOrigem;
    }

    public String getNomeDestinatario() {
        return nomeDestinatario;
    }

    public void setNomeDestinatario(String nomeDestinatario) {
        this.nomeDestinatario = nomeDestinatario;
    }

    public String getChavePixDestino() {
        return chavePixDestino;
    }

    public void setChavePixDestino(String chavePixDestino) {
        this.chavePixDestino = chavePixDestino;
    }

    public String getCpfDestinatario() {
        return cpfDestinatario;
    }

    public void setCpfDestinatario(String cpfDestinatario) {
        this.cpfDestinatario = cpfDestinatario;
    }




}
