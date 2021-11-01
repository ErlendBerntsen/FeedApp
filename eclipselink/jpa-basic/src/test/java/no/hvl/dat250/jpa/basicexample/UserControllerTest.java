package no.hvl.dat250.jpa.basicexample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.jpa.basicexample.dao.PollDAO;
import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
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

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PollDAO pollDAO;

    private final CredentialsDTO user = new CredentialsDTO(new Username("Espen"),new Password("password123"));

    @BeforeEach
    void setUp(){
        pollDAO.deleteAll();
        userDAO.deleteAll();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            authentication.setAuthenticated(false);
        }
    }

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

    @Test
    void successfulUserLoginShouldReturnJwt() throws Exception {
        CredentialsDTO credentials = new CredentialsDTO(new Username("test"), new Password("password"));
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)));

        var result  = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andReturn();
        String jwt = result.getResponse().getHeader("Authorization");
        assertNotNull(jwt);
        assertTrue(jwt.startsWith("Bearer "));
    }

    @Test
    void unsuccessfulUserLoginShouldReturnError() throws Exception {
        CredentialsDTO credentials = new CredentialsDTO(new Username("tes"), new Password("password"));
        var result  = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andReturn();
        String jwt = result.getResponse().getHeader("Authorization");
        assertNull(jwt);
    }


    @Test
    void authenticatedUserShouldBeAbleToAccessTheirOwnInformation() throws Exception{
        CredentialsDTO credentials = new CredentialsDTO(new Username("test"), new Password("password"));
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)));

        var result  = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andReturn();

        String jwt = result.getResponse().getHeader("Authorization");
        JsonNode jsonNode = new ObjectMapper()
                .readTree(result.getResponse().getContentAsString());
        var id = jsonNode.get("id").asLong();
        mockMvc.perform(get("/users/" + id).header("Authorization", jwt))
                .andExpect(status().isOk());
    }

    @Test
    void authenticatedUserShouldNotBeAbleToAccessOtherUserInformation() throws Exception{
        UserClass user = new UserClass();
        user.setUsername(new Username("Espen"));
        user.setPassword(new Password("password123"));
        userDAO.save(user);
        CredentialsDTO credentials = new CredentialsDTO(new Username("test"), new Password("password"));

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)));

        var result  = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andReturn();

        String jwt = result.getResponse().getHeader("Authorization");
        mockMvc.perform(get("/users/" + user.getId()).header("Authorization", jwt))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserShouldNotBeAbleToAccessOtherUserInformation() throws Exception{
        UserClass user = new UserClass();
        user.setUsername(new Username("Espen"));
        user.setPassword(new Password("password123"));
        userDAO.save(user);
        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isForbidden());
    }

    /*
    The update and delete tests dont test for authorization since they have exactly the same authorization
    configuration as the GET users/id request which has tests for authorization. These test only check
    that the functionality work as intended
     */
    @Test
    void updateUserGivesStatusOk() throws Exception {
        String userURL = mockMvc.perform(post("/users")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(user)))
            .andReturn().getResponse().getRedirectedUrl();

        UserClass userEntity = user.convertToUserEntity();
        userEntity.setUsername(new Username("Askeladd"));

        var loginResponse = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andReturn();

        String jwt = loginResponse.getResponse().getHeader("Authorization");

        MvcResult result = mockMvc.perform(put(userURL).header("Authorization", jwt)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(userEntity)))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        Username updatedUsername = new Username(jsonNode.get("username").asText());
        assertEquals(userEntity.getUsername(), updatedUsername);
    }

    @Test
    void deleteUserGivesStatusOK() throws Exception {
        String userURL = mockMvc.perform(post("/users")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(user)))
            .andReturn().getResponse().getRedirectedUrl();

        var loginResponse = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andReturn();

        String jwt = loginResponse.getResponse().getHeader("Authorization");

        mockMvc.perform(delete(userURL).header("Authorization", jwt))
            .andExpect(status().isOk());

        Long id = Long.parseLong(userURL.substring(userURL.length()-2));
        assertTrue(userDAO.findById(id).isEmpty());
    }


}
