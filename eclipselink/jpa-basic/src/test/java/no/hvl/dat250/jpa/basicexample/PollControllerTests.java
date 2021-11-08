package no.hvl.dat250.jpa.basicexample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.jpa.basicexample.dao.PollDAO;
import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.dto.CredentialsDTO;
import no.hvl.dat250.jpa.basicexample.dto.PollDTO;
import no.hvl.dat250.jpa.basicexample.dto.VoteDTO;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    private CredentialsDTO credentials = new CredentialsDTO(new Username("creator"), new Password("password"));

    private CredentialsDTO adminCredentials = new CredentialsDTO(new Username("admin"), new Password("password"));

    private VoteDTO vote = new VoteDTO();


    @BeforeEach
    void setup(){
        pollDAO.deleteAll();
        userDAO.deleteAll();
        vote.setVoteType(VoteType.GUEST);
        vote.setOptionChosen("yes");
    }


    @Test
    void unauthenticatedUserShouldNotCreatePoll() throws Exception{
        var creator = createUser(credentials);
        var poll = createPollDTO(creator);
        mockMvc.perform(post("/polls")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(poll)))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserShouldCreatePoll() throws Exception {
        var creator = createUser(credentials);
        var poll = createPollDTO(creator);
        String jwt = getJwtFromLoginRequest(credentials);
        mockMvc.perform(post("/polls").header("Authorization", jwt)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(poll)))
            .andExpect(status().isCreated());
    }

    //See deletion test for authorization tests

    @Test
    void updatePollGivesStatusIsOk() throws Exception {
        var creator = createUser(credentials);
        var poll = createPollDTO(creator);
        String pollURL = createPollAndGetURL(poll, credentials);

        poll.setIsPrivate(true);

        String jwt = getJwtFromLoginRequest(credentials);

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
        var creator = createUser(credentials);
        var poll = createPollDTO(creator);
        String pollURL = createPollAndGetURL(poll,credentials);

        String jwt = getJwtFromLoginRequest(credentials);

        mockMvc.perform(delete(pollURL).header("Authorization", jwt))
                .andExpect(status().isOk());
        Long pollId = Long.parseLong(pollURL.substring(pollURL.length()-2));

        assertTrue(pollDAO.findById(pollId).isEmpty());
    }

    @Test
    void authenticatedNonPollCreatorAndNonAdminUserCantDeletePoll() throws Exception {
        var creator = createUser(credentials);
        var poll = createPollDTO(creator);
        String pollURL = createPollAndGetURL(poll, credentials);
        var nonCreatorCredentials = new CredentialsDTO(new Username("test"), new Password("password"));
        createUser(nonCreatorCredentials);

        String jwt = getJwtFromLoginRequest(nonCreatorCredentials);
        mockMvc.perform(delete(pollURL).header("Authorization", jwt))
                .andExpect(status().isForbidden());

        Long pollId = Long.parseLong(pollURL.substring(pollURL.length()-2));
        assertTrue(pollDAO.findById(pollId).isPresent());
    }



    @Test
    void unauthenticatedUserCantDeletePoll() throws Exception {
        var creator = createUser(credentials);
        var poll = createPollDTO(creator);
        String pollURL = createPollAndGetURL(poll, credentials);

        mockMvc.perform(delete(pollURL))
                .andExpect(status().isForbidden());

        Long pollId = Long.parseLong(pollURL.substring(pollURL.length()-2));
        assertTrue(pollDAO.findById(pollId).isPresent());
    }

    @Test
    void adminShouldGetAllPolls() throws Exception{
        createUser(adminCredentials);
        String jwt = getJwtFromLoginRequest(adminCredentials);
        mockMvc.perform(get("/polls").header("Authorization", jwt))
                .andExpect(status().isOk());
    }

    @Test
    void nonAdminShouldNotGetAllPolls() throws Exception{
        CredentialsDTO nonAdminCredentials = new CredentialsDTO(new Username("nonadmin"),  new Password("password"));
        createUser(nonAdminCredentials);
        String jwt = getJwtFromLoginRequest(nonAdminCredentials);
        mockMvc.perform(get("/polls").header("Authorization", jwt))
                .andExpect(status().isForbidden());
    }

    @Test
    void nonAuthenticatedUserShouldNotGetAllPolls() throws Exception{
        mockMvc.perform(get("/polls"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldDeletePollTheyDidNotCreate() throws Exception{
        var creator = createUser(credentials);
        var poll = createPollDTO(creator);
        String pollURL = createPollAndGetURL(poll, credentials);

        createUser(adminCredentials);
        String jwt = getJwtFromLoginRequest(adminCredentials);

        mockMvc.perform(delete(pollURL).header("Authorization", jwt))
                .andExpect(status().isOk());
        Long pollId = Long.parseLong(pollURL.substring(pollURL.length()-2));

        assertTrue(pollDAO.findById(pollId).isEmpty());
    }

    @Test
    void getAllPublicPollsRequireNoAuthorization() throws Exception {
     mockMvc.perform(get("/polls/?isPrivate=false"))
                .andExpect(status().isOk());
    }

    @Test
    void getPollByCodeRequireNoAuthorization() throws Exception {
        Poll poll = new Poll();
        pollDAO.save(poll);
        var code = poll.getCode();
        mockMvc.perform(get("/polls/?code=" + code))
                .andExpect(status().isOk());
    }

    @Test
    void authenticatedUserCanGetTheirOwnPolls() throws Exception {
        var creator = createUser(credentials);
        var poll = createPollDTO(creator);
        createPollAndGetURL(poll, credentials);
        String jwt = getJwtFromLoginRequest(credentials);
        mockMvc.perform(get("/polls/?creator=" + creator.getId()).header("Authorization", jwt))
                .andExpect(status().isOk());
    }

    @Test
    void adminCanGetPollsCreatedByOtherUser() throws Exception {
        var creator = createUser(credentials);
        var poll = createPollDTO(creator);
        createPollAndGetURL(poll, credentials);
        createUser(adminCredentials);
        String jwt = getJwtFromLoginRequest(adminCredentials);
        mockMvc.perform(get("/polls/?creator=" + creator.getId()).header("Authorization", jwt))
                .andExpect(status().isOk());
    }

    @Test
    void authenticatedUserCanNotGetOtherUserPolls() throws Exception {
        var creator = createUser(adminCredentials);
        var poll = createPollDTO(creator);
        createPollAndGetURL(poll, adminCredentials);
        createUser(credentials);
        String jwt = getJwtFromLoginRequest(credentials);
        mockMvc.perform(get("/polls/?creator=" + creator.getId()).header("Authorization", jwt))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserCanNotGetOtherUserPolls() throws Exception {
        var creator = createUser(adminCredentials);
        var poll = createPollDTO(creator);
        createPollAndGetURL(poll, adminCredentials);
        mockMvc.perform(get("/polls/?creator=" + creator.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotBeAbleToVoteOnPollThatIsHasNotStarted() throws Exception {
        var poll = new Poll();
        poll.setVotingStart(Timestamp.valueOf("9999-09-20 12:00:00"));
        pollDAO.save(poll);

        mockMvc.perform(post("/polls/" + poll.getId() + "/votes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(vote)))
                .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotBeAbleToVoteOnPollThatIsHasEnded() throws Exception {
        var poll = new Poll();
        poll.setVotingStart(Timestamp.valueOf("1998-09-20 12:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("1999-09-20 12:00:00"));
        pollDAO.save(poll);

        mockMvc.perform(post("/polls/" + poll.getId() + "/votes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(vote)))
                .andExpect(status().isForbidden());
    }

    @Test
    void userShouldBeAbleToVoteOnPollThatHasStarted() throws Exception {
        var poll = new Poll();
        poll.setVotingStart(Timestamp.valueOf("1998-09-20 12:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("3999-09-20 12:00:00"));
        pollDAO.save(poll);

        mockMvc.perform(post("/polls/" + poll.getId() + "/votes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(vote)))
                .andExpect(status().isCreated());
    }


    private String getJwtFromLoginRequest(CredentialsDTO credentials) throws Exception{
        var loginResponse = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andReturn();
       return loginResponse.getResponse().getHeader("Authorization");
    }

    private String createPollAndGetURL(PollDTO poll, CredentialsDTO creatorCredentials) throws Exception {
        String jwt = getJwtFromLoginRequest(creatorCredentials);
        return mockMvc.perform(post("/polls").header("Authorization", jwt)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(poll)))
                .andReturn().getResponse().getRedirectedUrl();
    }

    private PollDTO createPollDTO(UserClass creator) {
        PollDTO poll = new PollDTO();
        poll.setIsPrivate(false);
        poll.setQuestion("What is the meaning of life?");
        poll.setVotingStart(Timestamp.valueOf("2021-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2021-09-30 00:00:00"));
        poll.setCreatorId(creator.getId());
        return poll;
    }

    private UserClass createUser(CredentialsDTO userCredentials) {
        UserClass creator = new UserClass();
        creator.setUsername(new Username(userCredentials.getUsername()));
        creator.setPassword(new Password(passwordEncoder.encode(userCredentials.getPassword())));
        if(userCredentials.equals(adminCredentials)){
            creator.setUserType(UserType.ADMIN);
        }
        userDAO.save(creator);
        return creator;
    }

}
