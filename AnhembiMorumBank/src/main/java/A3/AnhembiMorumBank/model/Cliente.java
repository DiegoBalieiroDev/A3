package A3.AnhembiMorumBank.model;

import A3.AnhembiMorumBank.DTO.Cliente.AtualizarClienteDTO;
import A3.AnhembiMorumBank.DTO.Cliente.ClienteDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String chavePix;

    private Boolean ativo = true;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Conta conta;

    @Column(name = "data_cadastro")
    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    private TipoCliente tipoCliente;

    @OneToMany(mappedBy = "clienteOrigem")
    @JsonManagedReference(value = "cliente-transacoes")
    private List<Transacao> transacoes;

    @Embedded
    private Endereco endereco;

    public Cliente() {
    }

    public Cliente(Long id, String nome, String email, String telefone, String cpf, Boolean ativo, LocalDateTime dataCadastro, TipoCliente tipoCliente, Endereco endereco) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.ativo = ativo;
        this.dataCadastro = dataCadastro;
        this.tipoCliente = tipoCliente;
        this.endereco = endereco;
    }

    public Cliente(ClienteDTO dados) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.telefone = dados.telefone();
        this.cpf = dados.cpf();
        this.ativo = true;
        this.chavePix = this.email;
        this.dataCadastro = LocalDateTime.now();
        this.tipoCliente = dados.tipoCliente();
        this.endereco = new Endereco(dados.endereco());
    }

    public Cliente(ClienteDTO dados, Conta conta) {
        this(dados);
        this.vincularConta(conta);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public void vincularConta(Conta conta) {
        this.conta = conta;
        conta.setCliente(this);
    }

    public void atualizarInformacoes(@Valid AtualizarClienteDTO dados) {
        if(dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.telefone() != null) {
            this.telefone = dados.telefone();
        }
        if (dados.endereco() != null) {
            this.endereco.atualizarInformacoesEndereco(dados.endereco());
        }
    }

    public void excluir() {
        this.ativo = false;
    }
}
