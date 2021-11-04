package no.hvl.dat250.jpa.basicexample;

import no.hvl.dat250.jpa.basicexample.dao.*;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import no.hvl.dat250.jpa.basicexample.entities.Vote;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DAOTests {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PollDAO pollDAO;


    @Autowired
    private VoteDAO voteDAO;

    private UserClass user;

    private Poll poll;
    private Vote vote;

    @Before
    public void setUp() {
        user = new UserClass();
        user.setUsername(new Username("TestUser1"));
        user.setPassword(new Password("password123"));
        user.setUserType(UserType.REGULAR);

        poll = new Poll ();
        poll.setIsPrivate(false);
        poll.setQuestion("Lorem ipsum?");
        poll.setVotingStart(Timestamp.valueOf("2020-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2020-09-30 00:00:00"));
        poll.setCode(123);

        vote = new Vote ();
        vote.setVoteType(VoteType.USER);
        vote.setOptionChosen("yes");

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
        user2.setUsername(new Username("TestUser2"));
        user2.setPassword(new Password("password456"));
        user2.setUserType(UserType.ADMIN);
        userDAO.save(user);
        userDAO.save(user2);
        List<UserClass> users = userDAO.findAll();
        assertEquals(2, users.size());
        assertTrue(users.contains(user));
        assertTrue(users.contains(user2));
    }

    @Test
    public void userShouldBeFoundByUsername(){
        userDAO.save(user);
        var userMaybe = userDAO.findByUsername(user.getUsername());
        assertTrue(userMaybe.isPresent());
        assertEquals(user, userMaybe.get());
    }

    @Test
    public void userShouldBeDeletedInDatabase(){
        userDAO.save(user);
        assertTrue(userDAO.findById(user.getId()).isPresent());
        userDAO.deleteById(user.getId());
        assertFalse(userDAO.findById(user.getId()).isPresent());
    }


    @Test
    public void shouldCreatePollInDatabase(){
        pollDAO.save(poll);
        Optional<Poll> pollMaybe = pollDAO.findById(poll.getId());
        assertTrue(pollMaybe.isPresent());
        assertEquals(poll, pollMaybe.get());
    }

    @Test
    public void shouldCreateVoteInDatabase(){
        voteDAO.save(vote);
        Optional<Vote> voteMaybe = voteDAO.findById(vote.getId());
        assertTrue(voteMaybe.isPresent());
        assertEquals(vote, voteMaybe.get());
    }

    @Test
    public void shouldCreateBidirectionalRelationBetweenUserAndPoll(){
        poll.addCreator(user);
        assertEquals(poll.getCreator().getId(), user.getId());
        assertEquals(user.getCreatedPolls().get(0).getId(), poll.getId() );
    }

    @Test
    public void shouldCreateUserForeignKeyInPollTable(){
        poll.addCreator(user);
        userDAO.save(user);
        pollDAO.save(poll);
        UserClass creator = pollDAO.getById(poll.getId()).getCreator();
        assertEquals(creator.getId(), user.getId());
    }

    @Test
    public void shouldCreateBidirectionalRelationBetweenPollAndVote(){
        vote.addPoll(poll);
        assertEquals(vote.getPoll().getId(), poll.getId());
        assertEquals(poll.getVotes().get(0).getId(), vote.getId() );
    }

    @Test
    public void shouldCreatePollForeignKeyInVoteTable(){
        vote.addPoll(poll);
        pollDAO.save(poll); //This also persists the vote
        assertTrue(voteDAO.findById(vote.getId()).isPresent());
        Poll p = voteDAO.findById(vote.getId()).get().getPoll();
        assertEquals(p.getId(), poll.getId());
    }

    @Test
    public void shouldCreateBidirectionalRelationBetweenUserAndVote(){
        vote.addVoter(user);
        assertEquals(vote.getVoter().getId(), user.getId());
        assertEquals(user.getVotes().get(0).getId(), vote.getId());
    }

    @Test
    public void shouldCreateUserForeignKeyInVoteTable(){
        vote.addVoter(user);
        userDAO.save(user); //This also persists the vote
        assertTrue(voteDAO.findById(vote.getId()).isPresent());
        UserClass voter = voteDAO.findById(vote.getId()).get().getVoter();
        assertEquals(voter.getId(), user.getId());
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
    public void onlyPollThatMatchesCodeShouldBeRetrieved(){
        Poll poll1 = new Poll();
        pollDAO.save(poll1);
        var pollMaybe = pollDAO.findByCode(poll1.getCode());
        assertTrue(pollMaybe.isPresent());
        assertEquals(pollMaybe.get().getCode(), poll1.getCode());
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
