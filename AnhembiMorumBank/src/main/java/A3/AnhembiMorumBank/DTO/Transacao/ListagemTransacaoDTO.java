package A3.AnhembiMorumBank.DTO.Transacao;

import A3.AnhembiMorumBank.model.Cliente;
import A3.AnhembiMorumBank.model.Transacao;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ListagemTransacaoDTO (
        Long id,
        String clienteOrigemNome,
        BigDecimal valor,
        String nomeDestinatario,
        String cpfDestinatario,
        String chavePixDestino,


        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
        LocalDateTime data,

        String status,
        boolean suspeitaGolpe

){
    public ListagemTransacaoDTO(Transacao t) {
        this(   t.getId(),
                t.getClienteOrigem().getNome(),
                t.getValor(),
                t.getNomeDestinatario(),
                t.getCpfDestinatario(),
                t.getChavePixDestino(),
                t.getData(),
                t.getStatus().name(),
                t.isSuspeitaGolpe());
        }

}
