package A3.AnhembiMorumBank;

import A3.AnhembiMorumBank.DTO.Transacao.TransacaoDTO;
import A3.AnhembiMorumBank.Service.*;
import A3.AnhembiMorumBank.Repository.*;
import A3.AnhembiMorumBank.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransacaoServiceTest {

    @InjectMocks
    private TransacaoService transacaoService;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private FraudeService fraudeService;

    @Mock
    private PasswordEncoder password;


    private Cliente criarCliente(Long id, String pinHash, String chavePix) {
        Cliente c = new Cliente();
        c.setId(id);
        c.setPin(pinHash);
        c.setChavePix(chavePix);
        c.setNome("Cliente Teste");
        c.setCpf("12345678900");
        return c;
    }

    private Conta criarConta(Cliente c, BigDecimal saldo) {
        Conta conta = new Conta();
        conta.setCliente(c);
        conta.setSaldo(saldo);
        return conta;
    }


    @Test
    void deveLancarErroQuandoClienteOrigemNaoExiste() {

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        TransacaoDTO dto = new TransacaoDTO(
                1L,
                BigDecimal.TEN,
                "pix@destino.com",
                null,
                null,
                "1234"
        );

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> transacaoService.realizarTransacao(dto));

        assertEquals("Cliente de origem não encontrado.", e.getMessage());
    }


    @Test
    void deveLancarErroPinIncorreto() {

        Cliente cliente = criarCliente(1L, "HASH", "pix123");
        Conta conta = criarConta(cliente, BigDecimal.TEN);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaRepository.findByCliente(cliente)).thenReturn(Optional.of(conta));

        when(password.matches("1234", "HASH")).thenReturn(false);

        TransacaoDTO dto = new TransacaoDTO(
                1L,
                BigDecimal.TEN,
                "pix@destino.com",
                null,
                null,
                "1234"
        );

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> transacaoService.realizarTransacao(dto));

        assertEquals("PIN incorreto.", e.getMessage());
    }


    @Test
    void deveLancarErroValorInvalido() {

        Cliente cliente = criarCliente(1L, "HASH", "pix123");
        Conta conta = criarConta(cliente, BigDecimal.TEN);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaRepository.findByCliente(cliente)).thenReturn(Optional.of(conta));
        when(password.matches(anyString(), anyString())).thenReturn(true);

        TransacaoDTO dto = new TransacaoDTO(
                1L,
                BigDecimal.ZERO,
                "pix@destino.com",
                null,
                null,
                "1234"
        );

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> transacaoService.realizarTransacao(dto));

        assertEquals("Valor inválido para transação.", e.getMessage());
    }

    @Test
    void deveLancarErroQuandoSaldoInsuficiente() {
        Cliente cliente = criarCliente(1L, "HASH", "pix1");
        Conta conta = criarConta(cliente, BigDecimal.valueOf(50));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaRepository.findByCliente(cliente)).thenReturn(Optional.of(conta));
        when(password.matches("1234", "HASH")).thenReturn(true);

        when(fraudeService.evaluate(any(), any(), any(), any(), any()))
                .thenReturn(new FraudResult(0, List.of(), FraudResult.Action.APPROVE));

        TransacaoDTO dto = new TransacaoDTO(
                1L,
                BigDecimal.valueOf(200),
                "dest",
                null,
                null,
                "1234"
        );

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> transacaoService.realizarTransacao(dto));

        assertEquals("Saldo insuficiente.", e.getMessage());
    }

    @Test
    void deveNegarTransacaoQuandoFraudeDeny() {
        Cliente cliente = criarCliente(1L, "HASH", "pix1");
        Conta conta = criarConta(cliente, BigDecimal.valueOf(500));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaRepository.findByCliente(cliente)).thenReturn(Optional.of(conta));
        when(password.matches(anyString(), anyString())).thenReturn(true);

        when(fraudeService.evaluate(any(), any(), any(), any(), any()))
                .thenReturn(new FraudResult(90, List.of("motivo"), FraudResult.Action.DENY));

        when(transacaoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TransacaoDTO dto = new TransacaoDTO(
                1L,
                BigDecimal.valueOf(10),
                "dest",
                null,
                null,
                "1234"
        );

        Transacao t = transacaoService.realizarTransacao(dto);

        assertEquals(StatusTransacao.NEGADA, t.getStatus());
        assertTrue(t.isSuspeitaGolpe());
    }

    @Test
    void deveAprovarTransacaoInterna() {
        Cliente origem = criarCliente(1L, "HASH", "pix1");
        Conta contaOrigem = criarConta(origem, BigDecimal.valueOf(100));

        Cliente destino = criarCliente(2L, "HASH2", "chaveDest");
        Conta contaDestino = criarConta(destino, BigDecimal.valueOf(50));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(origem));
        when(contaRepository.findByCliente(origem)).thenReturn(Optional.of(contaOrigem));
        when(clienteRepository.findByChavePix("chaveDest")).thenReturn(Optional.of(destino));
        when(contaRepository.findByCliente(destino)).thenReturn(Optional.of(contaDestino));
        when(password.matches(anyString(), anyString())).thenReturn(true);

        when(fraudeService.evaluate(any(), any(), any(), any(), any()))
                .thenReturn(new FraudResult(0, List.of(), FraudResult.Action.APPROVE));

        when(transacaoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TransacaoDTO dto = new TransacaoDTO(
                1L,
                BigDecimal.valueOf(30),
                "chaveDest",
                null,
                null,
                "1234"
        );

        Transacao t = transacaoService.realizarTransacao(dto);

        assertEquals(StatusTransacao.APROVADA, t.getStatus());
        assertEquals(new BigDecimal("70"), contaOrigem.getSaldo());
        assertEquals(new BigDecimal("80"), contaDestino.getSaldo());
    }
}
