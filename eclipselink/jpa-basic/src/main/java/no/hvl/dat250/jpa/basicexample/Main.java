package no.hvl.dat250.jpa.basicexample;

import no.hvl.dat250.jpa.basicexample.dao.PollDAOImpl;
import no.hvl.dat250.jpa.basicexample.dao.UserDAOImpl;
import no.hvl.dat250.jpa.basicexample.dao.VoteDAOImpl;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;

import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class Main {
    public static final String PERSISTENCE_UNIT_NAME = "votingsystem";
    private static UserDAOImpl userDAO;
    private static PollDAOImpl pollDAO;
    private static VoteDAOImpl voteDAO;


    public static void main(String[] args) {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        userDAO = new UserDAOImpl(em);
        pollDAO = new PollDAOImpl(em);
        voteDAO = new VoteDAOImpl(em);


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

        em.getTransaction().begin();
        em.createQuery("update UserClass u set u.username=:name where u.id=:id")
        .setParameter("name", "updated username")
        .setParameter("id", user.getId()).executeUpdate();
        em.getTransaction().commit();

        em.close();

    }
}
