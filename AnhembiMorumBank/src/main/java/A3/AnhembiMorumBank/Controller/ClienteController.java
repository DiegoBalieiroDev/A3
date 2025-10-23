package A3.AnhembiMorumBank.Controller;

import A3.AnhembiMorumBank.DTO.Cliente.AtualizarClienteDTO;
import A3.AnhembiMorumBank.DTO.Cliente.ClienteDTO;
import A3.AnhembiMorumBank.DTO.Cliente.ClienteListagemDTO;
import A3.AnhembiMorumBank.DTO.Cliente.DetalhamentoClienteDTO;
import A3.AnhembiMorumBank.Service.ClienteService;
import A3.AnhembiMorumBank.model.TipoCliente;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<Page<ClienteListagemDTO>> consultarClientesAtivos(
     @RequestParam(required = false) TipoCliente tipo,
     @PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {

        Page<ClienteListagemDTO> page;

        if (tipo != null) {
            page = service.listarClientePorTipo(tipo, paginacao);
        } else {
            page = service.listarClientes(paginacao);
        }

        return ResponseEntity.ok(page);
    }

    @PutMapping
    public ResponseEntity atualizarCliente(@RequestBody @Valid AtualizarClienteDTO dados) {
        var cliente = service.atualizarCliente(dados);

        return ResponseEntity.ok(new DetalhamentoClienteDTO(cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity inativarCliente(@PathVariable Long id) {
        service.deletarCliente(id);

        return ResponseEntity.noContent().build();
    }


}
