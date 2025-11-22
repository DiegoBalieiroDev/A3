package A3.AnhembiMorumBank.Service;

import A3.AnhembiMorumBank.DTO.Cliente.AtualizarClienteDTO;
import A3.AnhembiMorumBank.DTO.Cliente.ClienteDTO;
import A3.AnhembiMorumBank.Repository.ClienteRepository;
import A3.AnhembiMorumBank.Repository.UsuarioRepository;
import A3.AnhembiMorumBank.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (usuarioRepository.existsByLogin(dados.email())) {
            throw new RuntimeException("Já existe um usuário com esse e-mail.");
        }

//         cria cliente
        Cliente cliente = new Cliente(dados);

//         cria conta
        Conta conta = new Conta();
        conta.setNumeroConta(gerarNumeroConta());
        conta.setAgencia("0001");
        conta.setSaldo(new BigDecimal("1000.00"));
        conta.setCriadoEm(LocalDateTime.now());
        cliente.vincularConta(conta);

        cliente.setPin(password.encode(dados.pin()));

//         salva cliente
        repository.save(cliente);

//         cria usuario
        Usuario usuario = new Usuario();
        usuario.setLogin(cliente.getEmail());
        usuario.setSenha(password.encode(dados.senha()));
        usuario.setPerfil(Perfil.CLIENTE);
        usuario.setCliente(cliente);

//        salva info dos usuarios
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
