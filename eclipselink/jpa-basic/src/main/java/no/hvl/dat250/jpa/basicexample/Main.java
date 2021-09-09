package no.hvl.dat250.jpa.basicexample;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Main {
    private static final String PERSISTENCE_UNIT_NAME = "votingsystem";


    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        UserClass user = new UserClass();
        user.setUsername("TestUser1");
        user.setPassword("123");
        user.setUserType(UserType.REGULAR);


        UserClass user2 = new UserClass();
        user2.setUsername("TestUser2");
        user2.setPassword("456");
        user2.setUserType(UserType.ADMIN);

        Poll poll = new Poll ();
        poll.setIsPrivate(false);
        poll.setQuestion("Lorem ipsum?");
        poll.setVotingStart(Timestamp.valueOf("2020-09-20 00:00:00"));
        poll.setVotingEnd(Timestamp.valueOf("2020-09-30 00:00:00"));
        poll.addCreator(user);

        Vote vote = user.voteOnPoll(poll, "yes", false);
        Vote vote2 =user2.voteOnPoll(poll, "no", false);

        em.persist(poll);
        em.persist(user);
        em.persist(user2);
        em.persist(vote);
        em.persist(vote2);
        em.getTransaction().commit();

        Query q = em.createQuery("select u from UserClass u");
        List<UserClass> userList = q.getResultList();
        for (UserClass u : userList) {
            System.out.println(u);
        }


        Query q2 = em.createQuery("select p from Poll p");
        List<Poll> pollList = q2.getResultList();
        for (Poll p : pollList) {
            System.out.println(p);
        }

        Query q3 = em.createQuery("select v from Vote v");
        List<Vote> voteList = q3.getResultList();
        for (Vote v : voteList) {
            System.out.println(v);
        }
        em.close();
    }
}
