package no.hvl.dat250.jpa.basicexample;

import no.hvl.dat250.jpa.basicexample.dao.*;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import no.hvl.dat250.jpa.basicexample.entities.Vote;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DAOTests {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PollDAO pollDAO;

    private UserClass user;

    @Before
    public void setUp() {
        user = new UserClass();
        user.setUsername("TestUser1");
        user.setPassword("123");
        user.setUserType(UserType.REGULAR);

        pollDAO.deleteAll();
        userDAO.deleteAll();
    }

    @Test
    public void userShouldBeSavedAndFoundInDatabase(){
        userDAO.save(user);
        Optional<UserClass> userMaybe = userDAO.findById(user.getId());
        assertTrue(userMaybe.isPresent());
        assertEquals(user, userMaybe.get());
    }

    @Test
    public void nonPersistedUserShouldNotBeFoundInDatabase(){
        Optional<UserClass> userMaybe = userDAO.findById(-1L);
        assertFalse(userMaybe.isPresent());
    }

    @Test
    public void allUsersShouldBeFoundInDatabase(){
        UserClass user2 = new UserClass();
        user2.setUsername("TestUser2");
        user2.setPassword("456");
        user2.setUserType(UserType.ADMIN);
        userDAO.save(user);
        userDAO.save(user2);
        List<UserClass> users = userDAO.findAll();
        assertEquals(2, users.size());
        assertTrue(users.contains(user));
        assertTrue(users.contains(user2));
    }

    @Test
    public void userShouldBeDeletedInDatabase(){
        userDAO.save(user);
        assertTrue(userDAO.findById(user.getId()).isPresent());
        userDAO.deleteById(user.getId());
        assertFalse(userDAO.findById(user.getId()).isPresent());
    }

    @Test
    public void userShouldBeFoundByUsernameAndPasswordInDatabase(){
        userDAO.save(user);
        Optional<UserClass> userMaybe = userDAO.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        assertTrue(userMaybe.isPresent());
        assertEquals(user.getId(), userMaybe.get().getId());
    }

    @Test
    public void userShouldNotBeFoundWithWrongUsername(){
        userDAO.save(user);
        Optional<UserClass> userMaybe = userDAO.findByUsernameAndPassword("", user.getPassword());
        assertFalse(userMaybe.isPresent());
    }

    @Test
    public void userShouldNotBeFoundWithWrongPassword(){
        userDAO.save(user);
        Optional<UserClass> userMaybe = userDAO.findByUsernameAndPassword(user.getUsername(), "");
        assertFalse(userMaybe.isPresent());
    }

    @Test
    public void onlyPublicPollsShouldBeRetrieved(){
        Poll poll1 = new Poll();
        poll1.setIsPrivate(false);

        Poll poll2 = new Poll();
        poll2.setIsPrivate(true);

        pollDAO.save(poll1);
        pollDAO.save(poll2);

        List<Poll> publicPolls = pollDAO.findByIsPrivate(false);
        publicPolls.forEach(poll -> assertFalse(poll.getIsPrivate()));
    }

    @Test
    public void allVotesFromPollShouldBeFound(){
        Poll poll = new Poll();
        for(int i = 0; i < 10; i++) {
            Vote vote = new Vote();
            vote.addPoll(poll);
        }

        pollDAO.save(poll);
        var pollMaybe = pollDAO.findById(poll.getId());
        assertTrue(pollMaybe.isPresent());
        List<Vote> votes = pollMaybe.get().getVotes();
        assertEquals(poll.getVotes().size(), votes.size());
        poll.getVotes().forEach(vote -> assertTrue(votes.contains(vote)));
    }

}
