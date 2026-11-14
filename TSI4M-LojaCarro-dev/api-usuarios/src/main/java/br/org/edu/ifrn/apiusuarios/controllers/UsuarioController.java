package br.org.edu.ifrn.apiusuarios.controllers;

import br.org.edu.ifrn.apiusuarios.UsuarioException;
import br.org.edu.ifrn.apiusuarios.model.Cargo;
import br.org.edu.ifrn.apiusuarios.model.Usuario;
import br.org.edu.ifrn.apiusuarios.services.UsuarioService;
import br.org.edu.ifrn.apiusuarios.seguranca.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestBody Usuario usuario,
            @RequestHeader(value = "Cargo-Usuario-Logado", required = false) Cargo cargoLogado,
            @RequestHeader(value = "Id-Usuario-Logado", required = false) Long idLogado) {
        try {
            String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
            usuario.setSenha(senhaCriptografada);

            Usuario salvo = service.salvar(usuario, cargoLogado, idLogado);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (UsuarioException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> fazerLogin(@RequestBody Usuario credenciais) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(credenciais.getEmail(), credenciais.getSenha());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var token = tokenService.gerarToken((Usuario) auth.getPrincipal());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (UsuarioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(
            @PathVariable Long id,
            @RequestHeader(value = "Cargo-Usuario-Logado", required = false) Cargo cargoLogado,
            @RequestHeader(value = "Id-Usuario-Logado", required = false) Long idLogado) {
        try {
            service.deletar(id, cargoLogado, idLogado);
            return ResponseEntity.noContent().build();
        } catch (UsuarioException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}