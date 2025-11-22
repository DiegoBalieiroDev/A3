package A3.AnhembiMorumBank.Controller;

import A3.AnhembiMorumBank.DTO.Cliente.*;
import A3.AnhembiMorumBank.Service.ClienteService;
import A3.AnhembiMorumBank.model.Cliente;
import A3.AnhembiMorumBank.model.TipoCliente;
import A3.AnhembiMorumBank.model.Usuario;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @PostMapping
    public ResponseEntity<DetalhamentoClienteDTO> cadastrar(@RequestBody @Valid ClienteDTO clienteDTO, UriComponentsBuilder uriBuilder) {
        var cliente = service.cadastrarCliente(clienteDTO);

        var uri = uriBuilder.path("/clientes/{id}")
                .buildAndExpand(cliente.getId())
                .toUri();

        return ResponseEntity.created(uri).body(new DetalhamentoClienteDTO(cliente));
    }

    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/me")
    public ResponseEntity<DetalhamentoClienteDTO> getMe() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cliente cliente = service.buscarPorLogin(usuario.getLogin()); // login == email
        return ResponseEntity.ok(new DetalhamentoClienteDTO(cliente));
    }

    @SecurityRequirement(name = "bearer-key")
    @PutMapping
    public ResponseEntity atualizarCliente(@RequestBody @Valid AtualizarClienteDTO dados) {
        var cliente = service.atualizarCliente(dados);

        return ResponseEntity.ok(new DetalhamentoClienteDTO(cliente));
    }

    @SecurityRequirement(name = "bearer-key")
    @DeleteMapping("/{id}")
    public ResponseEntity inativarCliente(@PathVariable Long id) {
        service.deletarCliente(id);

        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "bearer-key")
    // confirmação de cliente tela pix
    @GetMapping("/pix/{chave}")
    public ResponseEntity<ClientePixDTO> buscarPorChavePix(@PathVariable String chave) {
        var cliente = service.buscarPorChavePix(chave);

        if (cliente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new ClientePixDTO(cliente.get()));
    }
}
