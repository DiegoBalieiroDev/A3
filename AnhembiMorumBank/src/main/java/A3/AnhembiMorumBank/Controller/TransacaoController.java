package A3.AnhembiMorumBank.Controller;

import A3.AnhembiMorumBank.DTO.Transacao.TransacaoDTO;
import A3.AnhembiMorumBank.DTO.Transacao.TransacaoResponseDTO;
import A3.AnhembiMorumBank.Service.TransacaoService;
import A3.AnhembiMorumBank.model.Transacao;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<TransacaoResponseDTO>> listarTodas() {
        List<TransacaoResponseDTO> lista = service.listarTodas()
                .stream()
                .map(TransacaoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
}
