package no.hvl.dat250.jpa.basicexample;

import no.hvl.dat250.jpa.basicexample.dao.PollDAOImpl;
import no.hvl.dat250.jpa.basicexample.dao.UserDAOImpl;
import no.hvl.dat250.jpa.basicexample.dao.VoteDAOImpl;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import no.hvl.dat250.jpa.basicexample.entities.Vote;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.Assert.*;

public class EntityDeletionRelationshipTests {

    private static final String PERSISTENCE_UNIT_NAME = "votingsystem";
    private static EntityManagerFactory factory;
    private EntityManager em;
    private UserDAOImpl userDAO;
    private UserClass user;
    private Poll poll;
    private Vote vote;
    private PollDAOImpl pollDAO;
    private VoteDAOImpl voteDAO;

    @Before
    public void setUp() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
        userDAO = new UserDAOImpl(em);
        userDAO.getAllUsers().forEach(user -> em.remove(user));
        pollDAO = new PollDAOImpl(em);
        pollDAO.getAllPolls().forEach(poll -> em.remove(poll));
        voteDAO = new VoteDAOImpl(em);
        voteDAO.getAllVotes().forEach(vote -> em.remove(vote));

        user = new UserClass();

        poll = new Poll ();
        poll.addCreator(user);
        vote = user.voteOnPoll(poll, "yes", VoteType.USER);
        userDAO.saveUser(user);
        pollDAO.savePoll(poll);
    }

    @After
    public void cleanUp(){
        em.close();
    }

    @Test
    public void deletingUserShouldNullifyCreatorInPoll(){
        userDAO.deleteUser(user.getId());
        assertTrue(pollDAO.getPollById(poll.getId()).isPresent());
        assertNull(pollDAO.getPollById(poll.getId()).get().getCreator());
    }

    @Test
    public void deletingUserShouldNullifyVoterInVoteAndChangeToGuestVote(){
        userDAO.deleteUser(user.getId());
        assertTrue(voteDAO.getVoteById(vote.getId()).isPresent());
        assertNull(voteDAO.getVoteById(vote.getId()).get().getVoter());
        assertEquals(VoteType.GUEST, voteDAO.getVoteById(vote.getId()).get().getVoteType());
    }

    @Test
    public void deletingPollShouldRemoveItFromCreatorsPollList(){
        pollDAO.deletePoll(poll.getId());
        assertTrue(userDAO.getUserById(user.getId()).isPresent());
        assertFalse(userDAO.getUserById(user.getId()).get().getCreatedPolls().contains(poll));
    }

    @Test
    public void deletingPollShouldDeleteAllVotesInIt(){
        Vote vote2 = new Vote();
        vote2.setPoll(poll);
        em.getTransaction().begin();
        em.persist(vote);
        em.getTransaction().commit();
        pollDAO.deletePoll(poll.getId());
        assertTrue(pollDAO.getAllVotesFromPollById(poll.getId()).isEmpty());
        assertTrue(voteDAO.getAllVotes().isEmpty());
    }

    @Test
    public void deletingVoteShouldDeleteVoteFromVotersVotes(){
        voteDAO.deleteVote(vote.getId());
        assertTrue(userDAO.getUserById(user.getId()).isPresent());
        assertTrue(userDAO.getUserById(user.getId()).get().getVotes().isEmpty());
    }

    @Test
    public void deletingVoteShouldDeleteVoteFromPoll(){
        voteDAO.deleteVote(vote.getId());
        assertTrue(pollDAO.getPollById(poll.getId()).isPresent());
        assertTrue(pollDAO.getPollById(poll.getId()).get().getVotes().isEmpty());
    }

}
