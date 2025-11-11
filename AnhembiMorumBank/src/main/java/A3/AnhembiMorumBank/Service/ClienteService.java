package A3.AnhembiMorumBank.Service;

import A3.AnhembiMorumBank.DTO.Cliente.AtualizarClienteDTO;
import A3.AnhembiMorumBank.DTO.Cliente.ClienteDTO;
import A3.AnhembiMorumBank.DTO.Cliente.ClienteListagemDTO;
import A3.AnhembiMorumBank.Repository.ClienteRepository;
import A3.AnhembiMorumBank.Repository.UsuarioRepository;
import A3.AnhembiMorumBank.model.Cliente;
import A3.AnhembiMorumBank.model.Conta;
import A3.AnhembiMorumBank.model.TipoCliente;
import A3.AnhembiMorumBank.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder password;

    @Transactional
    public Cliente cadastrarCliente(ClienteDTO dados) {
        Cliente cliente = new Cliente(dados);

        String numeroConta = gerarNumeroConta();
        String agencia = "0001"; // pode ser fixa ou gerar dinamicamente

        Conta conta = new Conta();
        conta.setNumeroConta(numeroConta);
        conta.setAgencia(agencia);
        conta.setSaldo(new BigDecimal("1000.00")); // saldo inicial de exemplo

        // vincula conta ao cliente
        cliente.vincularConta(conta);
        conta.setCriadoEm(LocalDateTime.now());

        repository.save(cliente);

        Usuario usuario = new Usuario();
        usuario.setLogin(cliente.getEmail());
        usuario.setSenha(password.encode(dados.senha()));

        usuarioRepository.save(usuario);

        return cliente;
    }

    private String gerarNumeroConta() {
        String numero;
        do {
            numero = String.format("%08d", (int) (Math.random() * 100_000_000)); // 8 dígitos
        } while (repository.existsByConta_NumeroConta(numero)); // garante unicidade
        return numero;
    }


    public Page<ClienteListagemDTO> listarClientes(Pageable paginacao) {
        return repository.findAllByAtivoTrue(paginacao)
                .map(ClienteListagemDTO::new);
    }


    public Page<ClienteListagemDTO> listarClientePorTipo(TipoCliente tipo, Pageable paginacao) {
        return repository.findAllByAtivoTrueAndTipoCliente(tipo, paginacao)
                .map(ClienteListagemDTO::new);
    }

    public Cliente buscarPorLogin(String login) {
        return repository.findByEmail(login)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para usuário logado."));
    }

    @Transactional
    public Cliente atualizarCliente(AtualizarClienteDTO dados) {
        var cliente = repository.getReferenceById(dados.id());
        cliente.atualizarInformacoes(dados);

        return cliente;
    }

    public void deletarCliente(Long id) {
        var medico = repository.getReferenceById(id);
        medico.excluir();
    }

    public Optional<Cliente> buscarPorChavePix(String chave) {
        return repository.findByChavePix(chave);
    }
}
