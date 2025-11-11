package A3.AnhembiMorumBank.Controller;


import A3.AnhembiMorumBank.Security.DadosAutenticacao;
import A3.AnhembiMorumBank.Security.DadosTokenJWT;
import A3.AnhembiMorumBank.Security.TokenService;
import A3.AnhembiMorumBank.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        // 1. Cria um token de autenticação (ainda não é o JWT) com login e senha.
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());

        // 2. O Spring Security (manager) valida no banco se o usuário e senha estão corretos.
        // Se estiverem errados, ele dispara uma exceção (BadCredentialsException).
        var authentication = manager.authenticate(authenticationToken);

        // 3. Se a autenticação deu certo, nosso TokenService gera o Token JWT assinado.
        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        // 4. Devolvemos o token JWT para o cliente, que deve guardá-lo.
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }

}
