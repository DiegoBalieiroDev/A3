package A3.AnhembiMorumBank.Service;

import A3.AnhembiMorumBank.Repository.ContaRepository;
import A3.AnhembiMorumBank.model.Conta;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ContaService {

    @Autowired
    private ContaRepository repository;

    public Conta consultarSaldo(Long contaId) {
        return repository.findById(contaId)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada."));
    }

    @Transactional
    public Conta depositar(Long contaId, BigDecimal valor) {
        Conta conta = repository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada."));

        conta.depositar(valor);
        repository.save(conta);

        return conta;
    }
}
