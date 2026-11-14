package br.org.edu.ifrn.LojaCarro.controllers;

import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import br.org.edu.ifrn.LojaCarro.services.CarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/carro")
public class CarroController {

    @Autowired
    private CarroService carroService;

    @Autowired
    private CarroRepository carroRepository;

    @PostMapping("/salvar")
    public ResponseEntity<Carro> salvarCarro(@RequestBody Carro c, @RequestHeader(value = "idUsuario", required = false) Long idUsuario) {
        Carro savedCarro = carroService.save(c, idUsuario);
        return ResponseEntity.ok(savedCarro);
    }

    @PostMapping(value = "/{id}/fotografia", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Carro> uploadFotografia(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "idUsuario", required = false) Long idUsuario) {

        Carro carro = carroService.salvarFotografia(id, file, idUsuario);
        return ResponseEntity.ok(carro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carro> atualizarCarro(@PathVariable Long id, @RequestBody Carro c, @RequestHeader(value = "idUsuario", required = false) Long idUsuario) {
        c.setId(id);
        Carro updatedCarro = carroService.update(c, idUsuario);
        return ResponseEntity.ok(updatedCarro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCarro(@PathVariable Long id, @RequestHeader(value = "idUsuario", required = false) Long idUsuario) {
        carroService.deleteById(id, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carro> pesquisarCarroPorId(@PathVariable Long id) {
        Optional<Carro> carro = carroService.findById(id);
        return carro.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Carro>> listarCarros() {
        List<Carro> carros = carroService.findAll();
        return ResponseEntity.ok(carros);
    }

    @GetMapping("/pesquisar")
    public ResponseEntity<List<Carro>> pesquisarPorMarcaEModelo(@RequestParam String marca, @RequestParam String modelo) {
        List<Carro> carros = carroRepository.findByMarcaAndModelo(marca, modelo);
        return ResponseEntity.ok(carros);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<Carro>> filtrarPorPrecoEAno(
            @RequestParam Double precoMin,
            @RequestParam Double precoMax,
            @RequestParam Integer anoFabricacao) {
        List<Carro> carros = carroRepository.findByPrecoBetweenAndAnoFabricacao(precoMin, precoMax, anoFabricacao);
        return ResponseEntity.ok(carros);
    }
}