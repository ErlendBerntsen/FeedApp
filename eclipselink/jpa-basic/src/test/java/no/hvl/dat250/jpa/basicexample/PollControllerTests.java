package no.hvl.dat250.jpa.basicexample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.jpa.basicexample.dao.PollDAO;
import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.dto.CredentialsDTO;
import no.hvl.dat250.jpa.basicexample.dto.PollDTO;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PollControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PollDAO pollDAO;

    private CredentialsDTO credentials = new CredentialsDTO(new Username("creator"), new Password("password"));

    @BeforeEach
    void setup(){
        pollDAO.deleteAll();
        userDAO.deleteAll();
    }

    @Test
    void getAllPollsGivesStatusOK() throws Exception {
        mockMvc.perform(get("/polls"))
                .andExpect(status().isOk());
    }

    @Test
    void createPollGivesStatusIsCreated() throws Exception {
        UserClass creator = new UserClass(new Username("Espen"), new Password("foobar123"), UserType.REGULAR);
        userDAO.save(creator);
        PollDTO poll = new PollDTO();
        poll.setIsPrivate(false);
        poll.setQuestion("What is the meaning of life?");
        poll.setVotingStart(Timestamp.valueOf("2021-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2021-09-30 00:00:00"));
        poll.setCreatorId(creator.getId());

        mockMvc.perform(post("/polls")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(poll)))
            .andExpect(status().isCreated());
    }

    //See deletion test for authorization tests

    @Test
    void updatePollGivesStatusIsOk() throws Exception {
        String userURL = mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andReturn().getResponse().getRedirectedUrl();
        Long id = Long.parseLong(userURL.substring(userURL.length()-2));

        PollDTO poll = new PollDTO();
        poll.setIsPrivate(false);
        poll.setQuestion("What is the meaning of life?");
        poll.setVotingStart(Timestamp.valueOf("2021-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2021-09-30 00:00:00"));
        poll.setCreatorId(id);

        String pollURL = mockMvc.perform(post("/polls")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(poll)))
                .andReturn().getResponse().getRedirectedUrl();

        poll.setIsPrivate(true);

        var loginResponse = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andReturn();

        String jwt = loginResponse.getResponse().getHeader("Authorization");

        var result = mockMvc.perform(put(pollURL).header("Authorization", jwt)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(poll)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        boolean updatedValue = jsonNode.get("isPrivate").asBoolean();
        assertEquals(poll.getIsPrivate(), updatedValue);
    }

    @Test
    void authenticatedPollCreatorCanDeletePoll() throws Exception {
        String userURL = mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andReturn().getResponse().getRedirectedUrl();
        Long id = Long.parseLong(userURL.substring(userURL.length()-2));

        PollDTO poll = new PollDTO();
        poll.setIsPrivate(false);
        poll.setQuestion("What is the meaning of life?");
        poll.setVotingStart(Timestamp.valueOf("2021-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2021-09-30 00:00:00"));
        poll.setCreatorId(id);

        String pollURL = mockMvc.perform(post("/polls")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(poll)))
            .andReturn().getResponse().getRedirectedUrl();

        var loginResponse = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andReturn();

        String jwt = loginResponse.getResponse().getHeader("Authorization");

        mockMvc.perform(delete(pollURL).header("Authorization", jwt))
                .andExpect(status().isOk());
        Long pollId = Long.parseLong(pollURL.substring(pollURL.length()-2));

        assertTrue(pollDAO.findById(pollId).isEmpty());
    }

    @Test
    void authenticatedNonPollCreatorUserCantDeletePoll() throws Exception {
        String userURL = mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andReturn().getResponse().getRedirectedUrl();
        Long id = Long.parseLong(userURL.substring(userURL.length()-2));

        PollDTO poll = new PollDTO();
        poll.setIsPrivate(false);
        poll.setQuestion("What is the meaning of life?");
        poll.setVotingStart(Timestamp.valueOf("2021-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2021-09-30 00:00:00"));
        poll.setCreatorId(id);

        String pollURL = mockMvc.perform(post("/polls")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(poll)))
                .andReturn().getResponse().getRedirectedUrl();

        var nonCreator = new CredentialsDTO(new Username("test"), new Password("password"));
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(nonCreator)));

        var loginResponse = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(nonCreator)))
                .andReturn();

        String jwt = loginResponse.getResponse().getHeader("Authorization");
        mockMvc.perform(delete(pollURL).header("Authorization", jwt))
                .andExpect(status().isForbidden());

        Long pollId = Long.parseLong(pollURL.substring(pollURL.length()-2));
        assertTrue(pollDAO.findById(pollId).isPresent());
    }

    @Test
    void unauthenticatedUserCantDeletePoll() throws Exception {
        String userURL = mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andReturn().getResponse().getRedirectedUrl();
        Long id = Long.parseLong(userURL.substring(userURL.length()-2));

        PollDTO poll = new PollDTO();
        poll.setIsPrivate(false);
        poll.setQuestion("What is the meaning of life?");
        poll.setVotingStart(Timestamp.valueOf("2021-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2021-09-30 00:00:00"));
        poll.setCreatorId(id);

        String pollURL = mockMvc.perform(post("/polls")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(poll)))
                .andReturn().getResponse().getRedirectedUrl();

        mockMvc.perform(delete(pollURL))
                .andExpect(status().isForbidden());

        Long pollId = Long.parseLong(pollURL.substring(pollURL.length()-2));
        assertTrue(pollDAO.findById(pollId).isPresent());
    }


}
