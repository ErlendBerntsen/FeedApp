package no.hvl.dat250.jpa.basicexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.dto.CredentialsDTO;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    CredentialsDTO user = new CredentialsDTO(new Username("Espen"),new Password("password123"));

    @Test
    void getAllUsersGivesStatusOK() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void createUserGivesStatusIsCreated() throws Exception {
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    //TODO rework delete and update test to handle authorization requirements
    /*
    @Test
    void updateUserGivesStatusOk() throws Exception {
        String userURL = mockMvc.perform(post("/users")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(user)))
            .andReturn().getResponse().getRedirectedUrl();
        UserClass userEntity = user.convertToUserEntity();
        userEntity.setUsername(new Username("Askeladd"));

        mockMvc.perform(put(userURL)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(userEntity)))
            .andExpect(status().isOk());
    }

    @Test
    void deleteUserGivesStatusOK() throws Exception {
        String userURL = mockMvc.perform(post("/users")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(user)))
            .andReturn().getResponse().getRedirectedUrl();

        mockMvc.perform(delete(userURL))
            .andExpect(status().isOk());
    }

     */
}
