package no.hvl.dat250.jpa.basicexample;

import no.hvl.dat250.jpa.basicexample.dao.*;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import no.hvl.dat250.jpa.basicexample.entities.Vote;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DAOTests {

    @Autowired
    private UserDAO userDAO1;

    @Autowired
    private PollDAO pollDAO1;

    private UserClass user;

    @Before
    public void setUp() {
        user = new UserClass();
        user.setUsername("TestUser1");
        user.setPassword("123");
        user.setUserType(UserType.REGULAR);

        pollDAO1.deleteAll();
        userDAO1.deleteAll();
    }

    @Test
    public void userShouldBeSavedAndFoundInDatabase(){
        userDAO1.save(user);
        Optional<UserClass> userMaybe = userDAO1.findById(user.getId());
        assertTrue(userMaybe.isPresent());
        assertEquals(user, userMaybe.get());
    }

    @Test
    public void nonPersistedUserShouldNotBeFoundInDatabase(){
        Optional<UserClass> userMaybe = userDAO1.findById(-1L);
        assertFalse(userMaybe.isPresent());
    }

    @Test
    public void allUsersShouldBeFoundInDatabase(){
        UserClass user2 = new UserClass();
        user2.setUsername("TestUser2");
        user2.setPassword("456");
        user2.setUserType(UserType.ADMIN);
        userDAO1.save(user);
        userDAO1.save(user2);
        List<UserClass> users = userDAO1.findAll();
        assertEquals(2, users.size());
        assertTrue(users.contains(user));
        assertTrue(users.contains(user2));
    }


    //TODO Make this a service test
//    @Test
//    public void userShouldBeUpdatedInDatabase(){
//        UserClass user2 = new UserClass();
//        user2.setUsername("TestUser2");
//        user2.setPassword("456");
//        user2.setUserType(UserType.ADMIN);
//        userDAO1.save(user);
//        userDAO.updateUser(user.getId(), user2);
//
//        Optional<UserClass> userMaybe = userDAO.getUserById(user.getId());
//        assertTrue(userMaybe.isPresent());
//
//        UserClass updatedUser = userMaybe.get();
//        assertEquals(user2.getUsername(), updatedUser.getUsername());
//        assertEquals(user2.getPassword(), updatedUser.getPassword());
//        assertEquals(user2.getUserType(), updatedUser.getUserType());
//    }

    @Test
    public void userShouldBeDeletedInDatabase(){
        userDAO1.save(user);
        assertTrue(userDAO1.findById(user.getId()).isPresent());
        userDAO1.deleteById(user.getId());
        assertFalse(userDAO1.findById(user.getId()).isPresent());
    }

    @Test
    public void userShouldBeFoundByUsernameAndPasswordInDatabase(){
        userDAO1.save(user);
        Optional<UserClass> userMaybe = userDAO1.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        assertTrue(userMaybe.isPresent());
        assertEquals(user.getId(), userMaybe.get().getId());
    }

    @Test
    public void userShouldNotBeFoundWithWrongUsername(){
        userDAO1.save(user);
        Optional<UserClass> userMaybe = userDAO1.findByUsernameAndPassword("", user.getPassword());
        assertFalse(userMaybe.isPresent());
    }

    @Test
    public void userShouldNotBeFoundWithWrongPassword(){
        userDAO1.save(user);
        Optional<UserClass> userMaybe = userDAO1.findByUsernameAndPassword(user.getUsername(), "");
        assertFalse(userMaybe.isPresent());
    }

    @Test
    public void onlyPublicPollsShouldBeRetrieved(){
        Poll poll1 = new Poll();
        poll1.setIsPrivate(false);

        Poll poll2 = new Poll();
        poll2.setIsPrivate(true);

        pollDAO1.save(poll1);
        pollDAO1.save(poll2);

        List<Poll> publicPolls = pollDAO1.findByIsPrivate(false);
        publicPolls.forEach(poll -> assertFalse(poll.getIsPrivate()));
    }


    //TODO Rework tests after creating new VoteDAO

//    @Test
//    public void pollShouldBeFoundByVoteId(){
//        Poll poll = new Poll();
//        Vote vote = new Vote();
//        vote.addPoll(poll);
//        em.getTransaction().begin();
//        em.persist(poll);
//        em.persist(vote);
//        em.getTransaction().commit();
//        Optional<Poll> pollMaybe = voteDAO.getPollFromVoteId(vote.getId());
//        assertTrue(pollMaybe.isPresent());
//        assertEquals(poll.getId(), pollMaybe.get().getId());
//    }
//
//    @Test
//    public void allVotesFromPollShouldBeFoundByPollId(){
//        Poll poll = new Poll();
//        pollDAO.savePoll(poll);
//        for(int i = 0; i < 10; i++) {
//            Vote vote = new Vote();
//            vote.addPoll(poll);
//            voteDAO.saveVote(vote);
//        }
//        Optional<List<Vote>> votesMaybe = pollDAO.getAllVotesFromPollById(poll.getId());
//        assertTrue(votesMaybe.isPresent());
//        List<Vote> votes = votesMaybe.get();
//        assertEquals(poll.getVotes().size(), votes.size());
//        poll.getVotes().forEach(vote -> assertTrue(votes.contains(vote)));
//    }

}
