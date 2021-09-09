package no.hvl.dat250.jpa.basicexample;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;

public class DatabaseTest {
    private static final String PERSISTENCE_UNIT_NAME = "votingsystem";
    private static EntityManagerFactory factory;
    private EntityManager em;

    private UserClass user;
    private Poll poll;
    private Vote vote;

    @Before
    public void setUp(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();

        user = new UserClass();
        user.setUsername("TestUser1");
        user.setPassword("123");
        user.setUserType(UserType.REGULAR);

        poll = new Poll ();
        poll.setIsPrivate(false);
        poll.setQuestion("Lorem ipsum?");
        poll.setVotingStart(Timestamp.valueOf("2020-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2020-09-30 00:00:00"));
        poll.setCode(123);

        vote = new Vote ();
        vote.setAnonVote(false);
        vote.setGuestVote(false);
        vote.setOptionChosen("yes");
    }

    @After
    public void cleanUp(){
        if(em != null) em.close();
    }

    @Test
    public void shouldCreateUserInDatabase(){
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        UserClass u = (UserClass) em.createQuery("select u from UserClass u").getSingleResult();
        assertEquals(user.getId(), u.getId());
    }

    @Test
    public void shouldCreatePollInDatabase(){
        em.getTransaction().begin();
        em.persist(poll);
        em.getTransaction().commit();
        Poll p = (Poll) em.createQuery("select p from Poll p").getSingleResult();
        assertEquals(poll.getId(), p.getId());
    }

    @Test
    public void shouldCreateVoteInDatabase(){
        em.getTransaction().begin();
        em.persist(vote);
        em.getTransaction().commit();
        Vote v = (Vote) em.createQuery("select v from Vote v").getSingleResult();
        assertEquals(vote.getId(), v.getId());
    }

}
