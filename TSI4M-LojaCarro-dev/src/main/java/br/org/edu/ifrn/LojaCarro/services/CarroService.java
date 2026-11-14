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
        Long idEntidade = (carro != null) ? carro.getId() : null;
        logService.enviarLog("CARRO", operacao, idEntidade, idUsuario);
    }

    public Carro save(Carro c, Long idUsuario) {
        validarCamposObrigatorios(c);
        validarPreco(c.getPreco());

        c.setSituacao("DISPONIVEL");

        Carro salvo = carroRepository.save(c);
        registrarLog("CRIAR", salvo, idUsuario);
        return salvo;
    }

    public Carro salvarFotografia(Long id, MultipartFile file, Long idUsuario) {
        if (file.isEmpty()) {
            throw new CarroException("O arquivo de imagem não pode estar vazio.");
        }

        String nomeOriginal = file.getOriginalFilename();
        if (nomeOriginal == null || (!nomeOriginal.toLowerCase().endsWith(".jpg") && !nomeOriginal.toLowerCase().endsWith(".png"))) {
            throw new CarroException("A fotografia deve ser no formato JPG ou PNG.");
        }

        Carro carro = carroRepository.findById(id).orElseThrow(() -> new CarroException("Carro não encontrado."));

        try {
            String novoNome = UUID.randomUUID().toString() + "_" + nomeOriginal;
            Path caminhoDestino = Paths.get("imagens").resolve(novoNome).toAbsolutePath();
            file.transferTo(caminhoDestino.toFile());

            carro.setFotografiaPrincipal(novoNome);
            Carro atualizado = carroRepository.save(carro);

            registrarLog("ATUALIZAR_FOTO", atualizado, idUsuario);
            return atualizado;
        } catch (IOException e) {
            throw new CarroException("Erro ao salvar a imagem no servidor.");
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