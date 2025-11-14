package A3.AnhembiMorumBank.Service;

import A3.AnhembiMorumBank.DTO.Cliente.ClienteListagemDTO;
import A3.AnhembiMorumBank.DTO.Transacao.TransacaoResponseDTO;
import A3.AnhembiMorumBank.Repository.ClienteRepository;
import A3.AnhembiMorumBank.Repository.TransacaoRepository;
import A3.AnhembiMorumBank.model.TipoCliente;
import A3.AnhembiMorumBank.model.Transacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    TransacaoRepository transacaoRepository;

    public Page<ClienteListagemDTO> listarClientes(Pageable paginacao) {
        return clienteRepository.findAllByAtivoTrue(paginacao)
                .map(ClienteListagemDTO::new);
    }


    public Page<ClienteListagemDTO> listarClientePorTipo(TipoCliente tipo, Pageable paginacao) {
        return clienteRepository.findAllByAtivoTrueAndTipoCliente(tipo, paginacao)
                .map(ClienteListagemDTO::new);
    }

}
