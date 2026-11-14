package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Classe CarroService")
public class CarroServiceTest {

    @Mock
    private CarroRepository carroRepository;

    @Mock
    private LogProducerService logService;

    @InjectMocks
    private CarroService carroService;

    private Carro carroValido;

    @BeforeEach
    void setUp() {
        carroValido = new Carro("Honda", "Civic", 2023, 2023, "Preta", 0, "Flex", "Automatica", 150000.0, "Novo", "DISPONIVEL", "civic.jpg");
    }

    @Test
    @DisplayName("save: Deve salvar carro com dados válidos")
    void testSaveCarroValido() {
        when(carroRepository.save(any(Carro.class))).thenReturn(carroValido);

        Carro resultado = carroService.save(carroValido, 1L);

        assertNotNull(resultado);
        assertEquals("Civic", resultado.getModelo());
        verify(carroRepository, times(1)).save(carroValido);
    }

    @Test
    @DisplayName("save: Deve lançar exceção quando marca é vazia")
    void testSaveCarroComMarcaVazia() {
        carroValido.setMarca("");

        CarroException exception = assertThrows(CarroException.class, () -> carroService.save(carroValido, 1L));

        assertEquals("A marca do carro é obrigatória.", exception.getMessage());
        verify(carroRepository, never()).save(any());
    }

    @Test
    @DisplayName("save: Deve lançar exceção quando modelo é nulo")
    void testSaveCarroComModeloNull() {
        carroValido.setModelo(null);

        CarroException exception = assertThrows(CarroException.class, () -> carroService.save(carroValido, 1L));

        assertEquals("O modelo do carro é obrigatório.", exception.getMessage());
        verify(carroRepository, never()).save(any());
    }

    @Test
    @DisplayName("save: Deve lançar exceção quando fotografia não é informada")
    void testSaveCarroSemFotografia() {
        carroValido.setFotografiaPrincipal(null);

        CarroException exception = assertThrows(CarroException.class, () -> carroService.save(carroValido, 1L));

        assertEquals("A fotografia do veículo é obrigatória.", exception.getMessage());
        verify(carroRepository, never()).save(any());
    }

    @Test
    @DisplayName("save: Deve lançar exceção quando preço for negativo")
    void testSaveCarroComPrecoNegativo() {
        carroValido.setPreco(-5000.0);

        CarroException exception = assertThrows(CarroException.class, () -> carroService.save(carroValido, 1L));

        assertTrue(exception.getMessage().contains("O preço do carro não pode ser nulo ou negativo"));
        verify(carroRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteById: Deve fazer exclusão lógica do carro")
    void testDeleteByIdValido() {
        Long idValido = 1L;
        carroValido.setId(idValido);
        when(carroRepository.findById(idValido)).thenReturn(Optional.of(carroValido));

        carroService.deleteById(idValido, 1L);

        verify(carroRepository, times(1)).save(carroValido);
        assertEquals("INATIVO", carroValido.getSituacao());
    }

    @Test
    @DisplayName("deleteById: Deve lançar exceção quando ID é negativo")
    void testDeleteByIdNegativo() {
        CarroException exception = assertThrows(CarroException.class, () -> carroService.deleteById(-1L, 1L));

        assertTrue(exception.getMessage().contains("O ID do carro não pode ser negativo ou zero"));
        verify(carroRepository, never()).findById(any());
    }

    @Test
    @DisplayName("findById: Deve retornar carro quando ID existe")
    void testFindByIdValido() {
        when(carroRepository.findById(1L)).thenReturn(Optional.of(carroValido));

        Optional<Carro> resultado = carroService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Civic", resultado.get().getModelo());
    }

    @Test
    @DisplayName("findById: Deve retornar Optional vazio quando não existe")
    void testFindByIdNaoExiste() {
        when(carroRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Carro> resultado = carroService.findById(999L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("findAll: Deve retornar lista de carros")
    void testFindAllComCarros() {
        Carro carro2 = new Carro("VW", "Gol", 2022, 2022, "Branco", 10000, "Flex", "Manual", 50000.0, "Usado", "DISPONIVEL", "gol.jpg");
        when(carroRepository.findAll()).thenReturn(Arrays.asList(carroValido, carro2));

        List<Carro> resultado = carroService.findAll();

        assertEquals(2, resultado.size());
        assertEquals("Civic", resultado.get(0).getModelo());
    }

    @Test
    @DisplayName("update: Deve atualizar carro válido")
    void testUpdateCarroValido() {
        carroValido.setId(1L);
        when(carroRepository.existsById(1L)).thenReturn(true);
        when(carroRepository.save(carroValido)).thenReturn(carroValido);

        Carro resultado = carroService.update(carroValido, 1L);

        assertNotNull(resultado);
        assertEquals("Civic", resultado.getModelo());
    }

    @Test
    @DisplayName("update: Deve lançar exceção quando ID é nulo")
    void testUpdateCarroSemId() {
        carroValido.setId(null);

        CarroException exception = assertThrows(CarroException.class, () -> carroService.update(carroValido, 1L));

        assertTrue(exception.getMessage().contains("não pode ser nulo"));
    }

    @Test
    @DisplayName("update: Deve lançar exceção quando carro não existe")
    void testUpdateCarroInexistente() {
        carroValido.setId(999L);
        when(carroRepository.existsById(999L)).thenReturn(false);

        CarroException exception = assertThrows(CarroException.class, () -> carroService.update(carroValido, 1L));

        assertTrue(exception.getMessage().contains("não encontrado para atualização"));
    }
}