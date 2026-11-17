package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CarroService {

    @Autowired
    public CarroRepository carroRepository;

    @Autowired
    private LogProducerService logService;

    private void registrarLog(String operacao, Carro carro, Long idUsuario) {
        try {
            Long idEntidade = (carro != null) ? carro.getId() : null;
            logService.enviarLog("CARRO", operacao, idEntidade, idUsuario);
        } catch (Exception e) {
            System.out.println("Aviso: Falha ao enviar log para o RabbitMQ.");
        }
    }

    public Carro save(Carro c, Long idUsuario) {
        validarCamposObrigatorios(c);
        validarPreco(c.getPreco());

        c.setSituacao("DISPONIVEL");

        Carro salvo = carroRepository.save(c);
        registrarLog("CRIAR", salvo, idUsuario);
        return salvo;
    }

    public Carro salvarFotografia(Long id, org.springframework.web.multipart.MultipartFile file, Long idUsuario) {
        Carro carro = carroRepository.findById(id).orElseThrow(() -> new RuntimeException("Carro não encontrado"));
        try {
            java.nio.file.Path pasta = java.nio.file.Paths.get("imagens").toAbsolutePath();
            if (!java.nio.file.Files.exists(pasta)) {
                java.nio.file.Files.createDirectories(pasta);
            }

            String nomeArquivo = java.util.UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            java.nio.file.Path caminhoDestino = pasta.resolve(nomeArquivo);
            java.nio.file.Files.copy(file.getInputStream(), caminhoDestino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            carro.setFotografiaPrincipal(nomeArquivo);
            return carroRepository.save(carro);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar imagem", e);
        }
    }

    public void deleteById(Long id, Long idUsuario) {
        if(id <= 0){
            throw new CarroException("O ID do carro não pode ser negativo ou zero.");
        }
        Optional<Carro> carro = carroRepository.findById(id);

        carro.ifPresent(c -> {
            c.setSituacao("INATIVO");
            carroRepository.save(c);
            registrarLog("EXCLUSAO_LOGICA", c, idUsuario);
        });
    }

    public Optional<Carro> findById(Long id) {
        if(id <= 0){
            throw new CarroException("O ID do carro não pode ser negativo ou zero.");
        }
        return carroRepository.findById(id);
    }

    public List<Carro> findAll() {
        return carroRepository.findAll();
    }

    public Carro update(Carro c, Long idUsuario) {
        if (c.getId() == null) {
            throw new CarroException("O ID do carro para atualização não pode ser nulo.");
        }
        if (!carroRepository.existsById(c.getId())) {
            throw new CarroException("Carro não encontrado para atualização.");
        }

        validarCamposObrigatorios(c);
        validarPreco(c.getPreco());

        Carro atualizado = carroRepository.save(c);
        registrarLog("ATUALIZAR", atualizado, idUsuario);
        return atualizado;
    }

    private void validarCamposObrigatorios(Carro c) {
        if (c.getMarca() == null || c.getMarca().trim().isEmpty()) {
            throw new CarroException("A marca do carro é obrigatória.");
        }
        if (c.getModelo() == null || c.getModelo().trim().isEmpty()) {
            throw new CarroException("O modelo do carro é obrigatório.");
        }
        if (c.getFotografiaPrincipal() == null || c.getFotografiaPrincipal().trim().isEmpty()) {
            throw new CarroException("A fotografia do veículo é obrigatória.");
        }
    }

    private void validarPreco(Double preco) {
        if (preco == null || preco < 0) {
            throw new CarroException("O preço do carro não pode ser nulo ou negativo.");
        }
    }
}