package A3.AnhembiMorumBank.Controller;

import A3.AnhembiMorumBank.DTO.Transacao.TransacaoDTO;
import A3.AnhembiMorumBank.DTO.Transacao.TransacaoResponseDTO;
import A3.AnhembiMorumBank.Service.TransacaoService;
import A3.AnhembiMorumBank.model.Transacao;
import A3.AnhembiMorumBank.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    TransacaoService service;

    @PostMapping
    public ResponseEntity<TransacaoResponseDTO> realizarTransacao(@RequestBody @Valid TransacaoDTO transacaoDTO) {
        var transacao = service.realizarTransacao(transacaoDTO);
        return ResponseEntity.ok(new TransacaoResponseDTO(transacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoResponseDTO> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(TransacaoResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<List<TransacaoResponseDTO>> listarMinhasTransacoes() {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String login = usuario.getLogin();

        List<Transacao> transacoes = service.listarTransacoesDoCliente(login);

        List<TransacaoResponseDTO> lista = transacoes.stream()
                .map(TransacaoResponseDTO::new)
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<TransacaoResponseDTO>> listarTodas() {
        List<TransacaoResponseDTO> lista = service.listarTodas()
                .stream()
                .map(TransacaoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovar(@PathVariable Long id) {
        service.aprovarTransacao(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/negar")
    public ResponseEntity<?> negar(@PathVariable Long id) {
        service.negarTransacao(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<TransacaoResponseDTO>> listarPendentes() {
        return ResponseEntity.ok(
                service.listarPendentes().stream()
                        .map(TransacaoResponseDTO::new)
                        .toList()
        );
    }

}
