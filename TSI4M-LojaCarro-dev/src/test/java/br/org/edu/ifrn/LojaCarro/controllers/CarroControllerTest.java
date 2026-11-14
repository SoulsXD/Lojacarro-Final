package br.org.edu.ifrn.LojaCarro.controllers;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import br.org.edu.ifrn.LojaCarro.services.CarroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarroController.class)
@DisplayName("Testes do CarroController")
public class CarroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarroService carroService;

    @MockBean
    private CarroRepository carroRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /carro/salvar - sucesso")
    void testSalvarCarroSucesso() throws Exception {
        Carro carro = new Carro("Honda", "Civic", 2023, 2023, "Preta", 0, "Flex", "Automatica", 150000.0, "Novo", "DISPONIVEL", "civic.jpg");
        carro.setId(1L);

        when(carroService.save(any(Carro.class), any())).thenReturn(carro);

        mockMvc.perform(post("/carro/salvar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carro)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Civic"));
    }

    @Test
    @DisplayName("POST /carro/salvar - modelo inválido")
    void testSalvarCarroModeloInvalido() throws Exception {
        Carro carro = new Carro("Honda", "", 2023, 2023, "Preta", 0, "Flex", "Automatica", 150000.0, "Novo", "DISPONIVEL", "civic.jpg");

        when(carroService.save(any(Carro.class), any())).thenThrow(new CarroException("O modelo do carro é obrigatório."));

        mockMvc.perform(post("/carro/salvar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carro)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /carro - listar todos")
    void testPesquisarTodosCarros() throws Exception {
        Carro c1 = new Carro("Honda", "Civic", 2023, 2023, "Preta", 0, "Flex", "Automatica", 150000.0, "Novo", "DISPONIVEL", "civic.jpg");
        c1.setId(1L);

        when(carroService.findAll()).thenReturn(Arrays.asList(c1));

        mockMvc.perform(get("/carro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].modelo").value("Civic"));
    }

    @Test
    @DisplayName("GET /carro/{id} - encontrado")
    void testPesquisarCarroPorIdEncontrado() throws Exception {
        Carro c = new Carro("Honda", "Civic", 2023, 2023, "Preta", 0, "Flex", "Automatica", 150000.0, "Novo", "DISPONIVEL", "civic.jpg");
        c.setId(1L);

        when(carroService.findById(1L)).thenReturn(Optional.of(c));

        mockMvc.perform(get("/carro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Civic"));
    }

    @Test
    @DisplayName("PUT /carro/{id} - atualizar")
    void testAtualizarCarro() throws Exception {
        Carro c = new Carro("Honda", "Civic", 2024, 2024, "Preta", 0, "Flex", "Automatica", 150000.0, "Novo", "DISPONIVEL", "civic.jpg");
        c.setId(1L);

        when(carroService.update(any(Carro.class), any())).thenReturn(c);

        mockMvc.perform(put("/carro/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.anoFabricacao").value(2024));
    }

    @Test
    @DisplayName("DELETE /carro/{id} - deletar")
    void testDeletarCarro() throws Exception {
        doNothing().when(carroService).deleteById(eq(1L), any());

        mockMvc.perform(delete("/carro/1"))
                .andExpect(status().isNoContent());
    }
}