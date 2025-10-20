package A3.AnhembiMorumBank.Controller;

import A3.AnhembiMorumBank.Repository.ContaRepository;
import A3.AnhembiMorumBank.Service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService service;


    @GetMapping("/{id}/saldo")
    public ResponseEntity<String> consultarSaldo(@PathVariable Long id) {
        BigDecimal saldo = service.consultarSaldo(id);
        return ResponseEntity.ok("Saldo atual: R$ " + saldo);
    }

    // POST /contas/1/depositar?valor=500
    @PostMapping("/{id}/depositar")
    public ResponseEntity<String> depositar(@PathVariable Long id, @RequestParam BigDecimal valor) {

        service.depositar(id, valor);
        return ResponseEntity.ok("Depósito de R$ " + valor + " realizado com sucesso!");
    }
}
