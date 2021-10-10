package no.hvl.dat250.jpa.basicexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
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


    @Test
    void getAllUsersGivesStatusOK() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void createUserGivesStatusIsCreated() throws Exception {
        UserClass user = new UserClass("Espen", "asd123", UserType.REGULAR);
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateUserGivesStatusOk() throws Exception {
        UserClass user = new UserClass("Espen", "asd123", UserType.REGULAR);
        String userURL = mockMvc.perform(post("/users")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(user)))
            .andReturn().getResponse().getRedirectedUrl();

        user.setUsername("Askeladd");

        mockMvc.perform(put(userURL)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk());
    }

    @Test
    void deleteUserGivesStatusOK() throws Exception {
        UserClass user = new UserClass("Espen", "asd123", UserType.REGULAR);
        String userURL = mockMvc.perform(post("/users")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(user)))
            .andReturn().getResponse().getRedirectedUrl();

        mockMvc.perform(delete(userURL))
            .andExpect(status().isOk());
    }


}
