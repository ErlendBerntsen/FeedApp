package no.hvl.dat250.jpa.basicexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;

import static org.junit.Assert.assertTrue;
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

    @Test
    void getAllPollsGivesStatusOK() throws Exception {
        mockMvc.perform(get("/polls"))
                .andExpect(status().isOk());
    }

    @Test
    void createPollGivesStatusIsCreated() throws Exception {
        UserClass creator = new UserClass(new Username("Espen"), new Password("foobar123"), UserType.REGULAR);
        Poll poll = new Poll ();
        poll.setIsPrivate(false);
        poll.setQuestion("What is the meaning of life?");
        poll.setVotingStart(Timestamp.valueOf("2021-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2021-09-30 00:00:00"));
        //poll.addCreator(creator); //Todo: adding creator reults in infinite recursion

        mockMvc.perform(post("/polls")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(poll)))
            .andExpect(status().isCreated());
    }

    //TODO rework delete and update test to handle authorization requirements

    /*

    @Test
    void updatePollGivesStatusIsOk() throws Exception {
        Poll poll = new Poll ();
        poll.setIsPrivate(false);
        poll.setQuestion("What is the meaning of life?");
        poll.setVotingStart(Timestamp.valueOf("2021-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2021-09-30 00:00:00"));

        String pollURL = mockMvc.perform(post("/polls")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(poll)))
                .andReturn().getResponse().getRedirectedUrl();

        poll.setIsPrivate(true);

        mockMvc.perform(put(pollURL)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(poll)))
                .andExpect(status().isOk());
    }


    @Test
    void deletePollGivesStatusIsOk() throws Exception {
        Poll poll = new Poll();
        poll.setIsPrivate(false);
        poll.setQuestion("What is the meaning of life?");
        poll.setVotingStart(Timestamp.valueOf("2021-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2021-09-30 00:00:00"));

        String pollURL = mockMvc.perform(post("/polls")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(poll)))
            .andReturn().getResponse().getRedirectedUrl();

        mockMvc.perform(delete(pollURL))
                .andExpect(status().isOk());
    }
     */
}
