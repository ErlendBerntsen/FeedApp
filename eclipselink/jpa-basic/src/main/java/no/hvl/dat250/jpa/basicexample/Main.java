package no.hvl.dat250.jpa.basicexample;

import no.hvl.dat250.jpa.basicexample.dao.*;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import no.hvl.dat250.jpa.basicexample.entities.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@SpringBootApplication
public class Main {
    public static final String PERSISTENCE_UNIT_NAME = "votingsystem";
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);


        /*
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        userDAO = new UserDAOImpl(em);
        pollDAO = new PollDAOImpl(em);
        voteDAO = new VoteDAOImpl(em);
        createTestData();
        printDatabase();
        em.close();

         */
    }

    @Bean
    public CommandLineRunner fillDatabase(UserDAO userDAO, PollDAO pollDAO){
        return args -> {
            UserClass user1 = new UserClass();
            user1.setUsername("Erlend");
            user1.setPassword("p4ssw0rd");
            user1.setUserType(UserType.ADMIN);

            UserClass user2 = new UserClass();
            user2.setUsername("Eivind");
            user2.setPassword("qwerty123");
            user2.setUserType(UserType.REGULAR);

            UserClass user3 = new UserClass();
            user3.setUsername("Arthur");
            user3.setPassword("2309480wfoklsdfj");
            user3.setUserType(UserType.REGULAR);

            UserClass user4 = new UserClass();
            user4.setUsername("Simen");
            user4.setPassword("nimeS321");
            user4.setUserType(UserType.REGULAR);

            Poll poll1 = new Poll ();
            poll1.setIsPrivate(false);
            poll1.setQuestion("Is pineapple on pizza allowed?");
            poll1.setVotingStart(Timestamp.valueOf("2021-09-20 00:00:00"));
            poll1.setVotingEnd(Timestamp.valueOf("2021-09-30 00:00:00"));
            poll1.addCreator(user1);

            Poll poll2 = new Poll ();
            poll2.setIsPrivate(true);
            poll2.setQuestion("Are dogs better than cats?");
            poll2.setVotingStart(Timestamp.valueOf("2021-12-12 12:00:00"));
            poll2.setVotingEnd(Timestamp.valueOf("2021-12-12 12:30:00"));
            poll2.addCreator(user2);

            Poll poll3 = new Poll ();
            poll3.setIsPrivate(false);
            poll3.setQuestion("Is there a war in Bas Sing Se?");
            poll3.setVotingStart(Timestamp.valueOf(LocalDateTime.now()));
            poll3.setVotingEnd(Timestamp.valueOf("2022-01-01 00:00:00"));

            user1.voteOnPoll(poll1, "yes", VoteType.USER);
            user2.voteOnPoll(poll1, "no", VoteType.ANONYMOUS);

            user1.voteOnPoll(poll2, "yes", VoteType.USER);
            user2.voteOnPoll(poll2, "yes", VoteType.USER);
            user3.voteOnPoll(poll2, "yes", VoteType.USER);
            user4.voteOnPoll(poll2, "no", VoteType.USER);

            Vote guestVote = new Vote();
            guestVote.setVoteType(VoteType.GUEST);
            guestVote.setOptionChosen("no");
            guestVote.addPoll(poll2);

            userDAO.save(user1);
            userDAO.save(user2);
            userDAO.save(user3);
            userDAO.save(user4);

            userDAO.findAll().forEach(user -> log.info("Created user: " + user.getUserStringWithoutPollsAndVotes()));

            pollDAO.save(poll1);
            pollDAO.save(poll2);
            pollDAO.save(poll3);

            pollDAO.findAll().forEach(poll -> log.info("Created poll: " + poll.toString()));




        };
    }

    private static void createTestData(){

        /*


        //Creating polls
        Poll poll1 = new Poll ();
        poll1.setIsPrivate(false);
        poll1.setQuestion("Is pineapple on pizza allowed?");
        poll1.setVotingStart(Timestamp.valueOf("2021-09-20 00:00:00"));
        poll1.setVotingEnd(Timestamp.valueOf("2021-09-30 00:00:00"));
        poll1.addCreator(user1);

        Poll poll2 = new Poll ();
        poll2.setIsPrivate(true);
        poll2.setQuestion("Are dogs better than cats?");
        poll2.setVotingStart(Timestamp.valueOf("2021-12-12 12:00:00"));
        poll2.setVotingEnd(Timestamp.valueOf("2021-12-12 12:30:00"));
        poll2.addCreator(user2);

        Poll poll3 = new Poll ();
        poll3.setIsPrivate(false);
        poll3.setQuestion("Is there a war in Bas Sing Se?");
        poll3.setVotingStart(Timestamp.valueOf(LocalDateTime.now()));
        poll3.setVotingEnd(Timestamp.valueOf("2022-01-01 00:00:00"));

        //Creating votes
        user1.voteOnPoll(poll1, "yes", VoteType.USER);
        user2.voteOnPoll(poll1, "no", VoteType.ANONYMOUS);

        user1.voteOnPoll(poll2, "yes", VoteType.USER);
        user2.voteOnPoll(poll2, "yes", VoteType.USER);
        user3.voteOnPoll(poll2, "yes", VoteType.USER);
        user4.voteOnPoll(poll2, "no", VoteType.USER);

        Vote guestVote = new Vote();
        guestVote.setVoteType(VoteType.GUEST);
        guestVote.setOptionChosen("no");
        guestVote.addPoll(poll2);

        //Persisting
        userDAO.saveUser(user1);
        userDAO.saveUser(user2);
        userDAO.saveUser(user3);
        userDAO.saveUser(user4);

        pollDAO.savePoll(poll1);
        pollDAO.savePoll(poll2);
        pollDAO.savePoll(poll3);

         */
    }


}
