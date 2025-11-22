package A3.AnhembiMorumBank.Controller;

import A3.AnhembiMorumBank.DTO.Cliente.ClienteListagemDTO;
import A3.AnhembiMorumBank.Service.AdminService;
import A3.AnhembiMorumBank.Service.ClienteService;
import A3.AnhembiMorumBank.model.TipoCliente;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearer-key")
public class AdminController {

    @Autowired
    AdminService service;

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


}
