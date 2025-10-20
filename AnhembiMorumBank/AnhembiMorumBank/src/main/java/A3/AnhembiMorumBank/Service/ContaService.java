package A3.AnhembiMorumBank.Service;

import A3.AnhembiMorumBank.Repository.ContaRepository;
import A3.AnhembiMorumBank.model.Conta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ContaService {

    @Autowired
    private ContaRepository repository;

    public BigDecimal consultarSaldo(Long contaId) {
        Conta conta = repository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada."));
        return conta.getSaldo();
    }

    @Transactional
    public void depositar(Long contaId, BigDecimal valor) {
        Conta conta = repository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada."));
        conta.depositar(valor);
        repository.save(conta);
    }
}
