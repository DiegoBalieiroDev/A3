package A3.AnhembiMorumBank.Controller;

import A3.AnhembiMorumBank.DTO.Transacao.TransacaoDTO;
import A3.AnhembiMorumBank.Service.TransacaoService;
import A3.AnhembiMorumBank.model.Transacao;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    TransacaoService service;

    @PostMapping
    public ResponseEntity<Transacao> realizarTransacao(@RequestBody @Valid TransacaoDTO transacaoDTO) {
        Transacao transacao = service.realizarTransacao(transacaoDTO);
        return ResponseEntity.ok(transacao);
    }
}
