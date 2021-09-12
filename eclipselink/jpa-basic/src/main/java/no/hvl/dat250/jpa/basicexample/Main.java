package no.hvl.dat250.jpa.basicexample;

import no.hvl.dat250.jpa.basicexample.DAO.PollDAOImpl;
import no.hvl.dat250.jpa.basicexample.DAO.UserDAOImpl;
import no.hvl.dat250.jpa.basicexample.DAO.VoteDAOImpl;

import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class Main {
    public static final String PERSISTENCE_UNIT_NAME = "votingsystem";


    public static void main(String[] args) {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        UserDAOImpl userDAO = new UserDAOImpl(em);
        PollDAOImpl pollDAO = new PollDAOImpl(em);
        VoteDAOImpl voteDAO = new VoteDAOImpl(em);


        //TODO Create more test data
        UserClass user = new UserClass();
        user.setUsername("TestUser1");
        user.setPassword("123");
        user.setUserType(UserType.REGULAR);

        Poll poll = new Poll ();
        poll.setIsPrivate(false);
        poll.setQuestion("Is pineapple on pizza allowed?");
        poll.setVotingStart(Timestamp.valueOf("2020-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2020-09-30 00:00:00"));
        poll.addCreator(user);

        user.voteOnPoll(poll, "yes", VoteType.USER);
        userDAO.saveUser(user);
        pollDAO.savePoll(poll);

        em.close();

    }
}
