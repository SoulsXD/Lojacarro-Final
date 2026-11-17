package br.org.edu.ifrn.apiusuarios.controllers;

import br.org.edu.ifrn.apiusuarios.model.LogOperacao;
import br.org.edu.ifrn.apiusuarios.repository.LogOperacaoRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class LogController {

    @Autowired
    private LogOperacaoRepository logRepository;

    @GetMapping
    public ResponseEntity<List<LogOperacao>> listarLogsAdm() {
        List<LogOperacao> logs = logRepository.findAll();
        return ResponseEntity.ok(logs);
    }
}