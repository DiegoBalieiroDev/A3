package A3.AnhembiMorumBank.Controller;

import A3.AnhembiMorumBank.DTO.Cliente.AtualizarClienteDTO;
import A3.AnhembiMorumBank.DTO.Cliente.ClienteDTO;
import A3.AnhembiMorumBank.DTO.Cliente.ClienteListagemDTO;
import A3.AnhembiMorumBank.Service.ClienteService;
import A3.AnhembiMorumBank.model.TipoCliente;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody @Valid ClienteDTO cliente) {
        service.cadastrarCliente(cliente);
        return ResponseEntity.ok("Cliente Cadastrado com sucesso");
    }

    @GetMapping
    public Page<ClienteListagemDTO> consultarClientesAtivos(
     @RequestParam(required = false) TipoCliente tipo,
     @PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        if (tipo != null) {
            return service.listarClientePorTipo(tipo, paginacao);
        } else {
            return service.listarClientes(paginacao);
        }
    }

    @PutMapping
    public void atualizarCliente(@RequestBody @Valid AtualizarClienteDTO dados) {
        service.atualizarCliente(dados);
    }

    @DeleteMapping
    public void inativarCliente(@PathVariable Long id) {
        service.deletarCliente(id);
    }


}
