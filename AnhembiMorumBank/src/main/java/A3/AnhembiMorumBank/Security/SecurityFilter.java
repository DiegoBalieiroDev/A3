package A3.AnhembiMorumBank.Security;

import A3.AnhembiMorumBank.Repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Tenta recuperar o token do cabeçalho "Authorization".
        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            // 2. Valida a assinatura do token e pega o "subject" (o login do usuário).
            var verificarJWT = tokenService.getSubject(tokenJWT);

            // 3. Busca o usuário no banco de dados.
            var usuario = repository.findByLogin(verificarJWT);

            // 4. "Força" a autenticação: Informa ao Spring que este usuário está
            // autenticado para esta requisição específica.
            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }


        // 5. Libera a requisição para seguir para o Controller.
        filterChain.doFilter(request, response);

    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;

    }
}



