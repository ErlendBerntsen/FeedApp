package no.hvl.dat250.jpa.basicexample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.jpa.basicexample.dao.PollDAO;
import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.dto.CredentialsDTO;
import no.hvl.dat250.jpa.basicexample.dto.UserDTO;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PollDAO pollDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final CredentialsDTO userCredentials = new CredentialsDTO(new Username("Espen"),new Password("password123"));

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
    void createUserGivesStatusIsCreated() throws Exception {
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userCredentials)))
                .andExpect(status().isCreated());
    }

    @Test
    void creatingUserWithTakenUsernameShouldGiveError() throws Exception {
        createUser(userCredentials, false);
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userCredentials)))
                .andExpect(status().isConflict());
    }

    @Test
    void successfulUserLoginShouldReturnJwt() throws Exception {
        createUser(userCredentials, false);
        String jwt = getJwtFromLoginRequest(userCredentials);
        assertNotNull(jwt);
        assertTrue(jwt.startsWith("Bearer "));
    }

    @Test
    void unsuccessfulUserLoginShouldNotReturnJwt() throws Exception {
        createUser(userCredentials, false);
        CredentialsDTO credentials = new CredentialsDTO(new Username("tes"), new Password("passwordd"));
        String jwt = getJwtFromLoginRequest(credentials);
        assertNull(jwt);
    }

    @Test
    void authenticatedUserShouldBeAbleToAccessTheirOwnInformation() throws Exception{
        var user = createUser(userCredentials, false);
        String jwt = getJwtFromLoginRequest(userCredentials);
        mockMvc.perform(get("/users/" + user.getId()).header("Authorization", jwt))
                .andExpect(status().isOk());
    }

    @Test
    void authenticatedUserShouldNotBeAbleToAccessOtherUserInformation() throws Exception{
        var user = createUser(userCredentials, false);
        CredentialsDTO otherUserCredentials = new CredentialsDTO(new Username("test"), new Password("password"));
        createUser(otherUserCredentials, false);
        String jwt = getJwtFromLoginRequest(otherUserCredentials);
        mockMvc.perform(get("/users/" + user.getId()).header("Authorization", jwt))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserShouldNotBeAbleToAccessOtherUserInformation() throws Exception{
        var user = createUser(userCredentials, false);
        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldBeAbleToAccessOtherUserInformation() throws Exception {
        var user = createUser(userCredentials, false);
        CredentialsDTO adminCredentials = new CredentialsDTO(new Username("admin"), new Password("password"));
        createUser(adminCredentials, true);
        String jwt = getJwtFromLoginRequest(adminCredentials);
        mockMvc.perform(get("/users/" + user.getId()).header("Authorization", jwt))
                .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedUserShouldNotBeAbleToGetAllUsers() throws Exception{
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedRegularUserShouldNotBeAbleToGetAllUsers() throws Exception{
        createUser(userCredentials, false);
        String jwt = getJwtFromLoginRequest(userCredentials);
        mockMvc.perform(get("/users").header("Authorization", jwt))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldBeAbleToGetAllUsers() throws Exception{
        CredentialsDTO adminCredentials = new CredentialsDTO(new Username("admin"), new Password("password"));
        createUser(adminCredentials, true);
        String jwt = getJwtFromLoginRequest(adminCredentials);
        mockMvc.perform(get("/users").header("Authorization", jwt))
                .andExpect(status().isOk());
    }


    /*
    The update and delete tests dont test for authorization since they have exactly the same authorization
    configuration as the GET users/id request which has tests for authorization. These test only check
    that the functionality work as intended
     */
    @Test
    void updateUserGivesStatusOk() throws Exception {
        var user = createUser(userCredentials, false);
        var userDTO = new UserDTO(null, new Username("Askeladd"), null, null, null, null);
        user.setUsername(new Username("Askeladd"));

        String jwt = getJwtFromLoginRequest(userCredentials);

        MvcResult result = mockMvc.perform(put("/users/" + user.getId()).header("Authorization", jwt)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(userDTO)))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        Username updatedUsername = new Username(jsonNode.get("username").asText());
        assertEquals(user.getUsername(), updatedUsername);
    }

    @Test
    void deleteUserGivesStatusOK() throws Exception {
       var user = createUser(userCredentials, false);
        String jwt = getJwtFromLoginRequest(userCredentials);
        mockMvc.perform(delete("/users/" + user.getId()).header("Authorization", jwt))
            .andExpect(status().isOk());
        assertTrue(userDAO.findById(user.getId()).isEmpty());
    }


    private String getJwtFromLoginRequest(CredentialsDTO credentials) throws Exception {
        var result = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andReturn();

        return result.getResponse().getHeader("Authorization");
    }

    private UserClass createUser(CredentialsDTO credentials, boolean isAdmin) {
        UserClass user = new UserClass();
        user.setUsername(new Username(credentials.getUsername()));
        user.setPassword(new Password(passwordEncoder.encode(credentials.getPassword())));
        if(isAdmin){
            user.setUserType(UserType.ADMIN);
        }
        userDAO.save(user);
        return user;
    }

}
