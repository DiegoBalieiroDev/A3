package A3.AnhembiMorumBank.Exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

@ControllerAdvice
public class TrataErros {

    // trata quando não acha o dado no bd
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarErro404(){
        return ResponseEntity.notFound().build();
    }

    // trata quando há duplicidade de dados
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity tratarErro500(SQLIntegrityConstraintViolationException exception){
        var resposta = Map.of(
                "message", "Erro de integridade no banco de dados: " + exception.getMessage());

        return ResponseEntity.badRequest().body(resposta);
    }

    // trata quando argumentos não são válidos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarErro400(MethodArgumentNotValidException exception){

        var erros = exception.getFieldErrors();

        return ResponseEntity.badRequest().body(erros.stream()
                .map(DadosErroValidacao::new)
                .toList());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity tratarErroBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity tratarErroAuthentication() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Falha na autenticação");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity tratarErroAcessoNegado() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity tratarErro500(Exception ex) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " +ex.getLocalizedMessage());
//    }

    private record DadosErroValidacao(
            String campo,
            String mensagem
    ) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

}
