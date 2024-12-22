package boraldan.backspark.storagesystem;



import boraldan.backspark.storagesystem.controller.SockController;
import boraldan.backspark.storagesystem.domen.Sock;
import boraldan.backspark.storagesystem.service.api.SockService;
import boraldan.backspark.storagesystem.tool.dto.SockDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SockController.class)
class SockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SockService sockService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testIncome() throws Exception {
        SockDto sockDto = new SockDto("model1", "red", 50, 100, true);
        Sock sock = new Sock(UUID.randomUUID(), "model1", "red", 50, 100, true);

        Mockito.when(sockService.registerIncome(any(SockDto.class))).thenReturn(sock);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model", is("model1")))
                .andExpect(jsonPath("$.color", is("red")));
    }

    @Test
    void testRegisterOutcome() throws Exception {
        SockDto sockDto = new SockDto("model1", "red", 50, 10, true);
        Sock sock = new Sock(UUID.randomUUID(), "model1", "red", 50, 90, true);

        Mockito.when(sockService.registerOutcome(any(SockDto.class))).thenReturn(sock);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(90)));
    }

    @Test
    void testGetAll() throws Exception {
        Sock sock = new Sock(UUID.randomUUID(), "model1", "green", 50, 100, true);
        Page<Sock> page = new PageImpl<>(Collections.singletonList(sock));
        Mockito.when(sockService.getSocks(anyString(), any(), any(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/socks/all")
                        .param("color", "green")
                        .param("minCottonPercentage", "0")
                        .param("maxCottonPercentage", "100")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].color", is("green")));
    }

    @Test
    void testGetTotalSocks() throws Exception {
        Mockito.when(sockService.getTotalSocks(anyString(), anyString(), any())).thenReturn(100);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/socks")
                        .param("color", "green")
                        .param("operation", "morethan")
                        .param("cottonPercentage", "50"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    void testUpdateSock() throws Exception {
        SockDto sockDto = new SockDto("model2", "blue", 40, 200, true);
        Sock sock = new Sock(UUID.randomUUID(), "model2", "blue", 40, 200, true);

        Mockito.when(sockService.updateSock(any(UUID.class), any(SockDto.class))).thenReturn(sock);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/socks/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model", is("model2")))
                .andExpect(jsonPath("$.color", is("blue")));
    }
}
