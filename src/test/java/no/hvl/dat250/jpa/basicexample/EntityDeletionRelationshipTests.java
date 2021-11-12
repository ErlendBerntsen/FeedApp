package no.hvl.dat250.jpa.basicexample;


import no.hvl.dat250.jpa.basicexample.dao.PollDAO;
import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
import no.hvl.dat250.jpa.basicexample.dao.VoteDAO;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import no.hvl.dat250.jpa.basicexample.entities.Vote;
import no.hvl.dat250.jpa.basicexample.services.PollService;
import no.hvl.dat250.jpa.basicexample.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;


//TODO fix these tests again. Spring is being weird with not wanting to autowire the services :(
//@RunWith(SpringRunner.class)
//@DataJpaTest
//public class EntityDeletionRelationshipTests {
//    private UserClass user;
//    private Poll poll;
//    private Vote vote;
//
//
//    @Autowired
//    private UserDAO userDAO1;
//
//    @Autowired
//    private PollDAO pollDAO1;
//
//    @Autowired
//    private VoteDAO voteDAO1;
//
//    @Autowired
//    private PollService pollService;
//
//    @Autowired
//    private UserService userService;
//
//
//    @Before
//    public void setUp() {
//        pollService.deleteAllPolls();
//        userService.deleteAllUsers();
//        voteDAO1.deleteAll();
//        pollDAO1.deleteAll();
//        userDAO1.deleteAll();
//
//        user = new UserClass();
//
//        poll = new Poll ();
//        poll.addCreator(user);
//        vote = user.voteOnPoll(poll, "yes", VoteType.USER);
//
//        userService.createUser(user);
//        pollService.createPoll(poll);
//        userDAO1.save(user);
//        pollDAO1.save(poll);
//    }
//
//    @Test
//    public void deletingUserShouldNullifyCreatorInPoll(){
//
//        userService.deleteUser(user.getId());
//        assertTrue(pollService.getPoll(poll.getId()).isPresent());
//        assertNull(pollService.getPoll(poll.getId()).get().getCreator());
//    }
//
//    @Test
//    public void deletingUserShouldNullifyVoterInVoteAndChangeToGuestVote(){
//        userDAO.deleteUser(user.getId());
//        assertTrue(voteDAO.getVoteById(vote.getId()).isPresent());
//        assertNull(voteDAO.getVoteById(vote.getId()).get().getVoter());
//        assertEquals(VoteType.GUEST, voteDAO.getVoteById(vote.getId()).get().getVoteType());
//    }
//
//    @Test
//    public void deletingPollShouldRemoveItFromCreatorsPollList(){
//        pollDAO.deletePoll(poll.getId());
//        assertTrue(userDAO.getUserById(user.getId()).isPresent());
//        assertFalse(userDAO.getUserById(user.getId()).get().getCreatedPolls().contains(poll));
//    }
//
//    @Test
//    public void deletingPollShouldDeleteAllVotesInIt(){
//        Vote vote2 = new Vote();
//        vote2.setPoll(poll);
//        em.getTransaction().begin();
//        em.persist(vote);
//        em.getTransaction().commit();
//        pollDAO.deletePoll(poll.getId());
//        assertTrue(pollDAO.getAllVotesFromPollById(poll.getId()).isEmpty());
//        assertTrue(voteDAO.getAllVotes().isEmpty());
//    }
//
//    @Test
//    public void deletingVoteShouldDeleteVoteFromVotersVotes(){
//        voteDAO.deleteVote(vote.getId());
//        assertTrue(userDAO.getUserById(user.getId()).isPresent());
//        assertTrue(userDAO.getUserById(user.getId()).get().getVotes().isEmpty());
//    }
//
//    @Test
//    public void deletingVoteShouldDeleteVoteFromPoll(){
//        voteDAO.deleteVote(vote.getId());
//        assertTrue(pollDAO.getPollById(poll.getId()).isPresent());
//        assertTrue(pollDAO.getPollById(poll.getId()).get().getVotes().isEmpty());
//    }
//
//}
