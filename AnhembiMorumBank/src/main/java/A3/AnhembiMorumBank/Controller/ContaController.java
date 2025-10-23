package A3.AnhembiMorumBank.Controller;

import A3.AnhembiMorumBank.DTO.ContaDTO.DepositarDTO;
import A3.AnhembiMorumBank.DTO.ContaDTO.SaldoDTO;
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
    public ResponseEntity<SaldoDTO> consultarSaldo(@PathVariable Long id) {
        var saldo = service.consultarSaldo(id);
        return ResponseEntity.ok(new SaldoDTO(saldo));
    }

    // POST /contas/1/depositar?valor=500
    @PostMapping("/{id}/depositar")
    public ResponseEntity<DepositarDTO> depositar(@PathVariable Long id, @RequestParam BigDecimal valor) {

        var conta = service.depositar(id, valor);

        return ResponseEntity.ok(new DepositarDTO(conta, valor));
    }
}
