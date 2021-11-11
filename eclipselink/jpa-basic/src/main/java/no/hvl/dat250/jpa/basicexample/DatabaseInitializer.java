package no.hvl.dat250.jpa.basicexample;

import no.hvl.dat250.jpa.basicexample.dao.PollDAO;
import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.entities.Poll;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import no.hvl.dat250.jpa.basicexample.entities.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class DatabaseInitializer implements CommandLineRunner{

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PollDAO pollDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        var user1 = new UserClass();
        user1.setUsername(new Username("Erlend"));
        user1.setPassword(new Password(passwordEncoder.encode("p4ssw0rd")));
        user1.setUserType(UserType.ADMIN);

        var user2 = new UserClass();
        user2.setUsername(new Username("Eivind"));
        user2.setPassword(new Password(passwordEncoder.encode("qwerty123")));
        user2.setUserType(UserType.REGULAR);

        var user3 = new UserClass();
        user3.setUsername(new Username("Arthur"));
        user3.setPassword(new Password(passwordEncoder.encode("2309480wfoklsdfj")));
        user3.setUserType(UserType.REGULAR);

        var user4 = new UserClass();
        user4.setUsername(new Username("Simen"));
        user4.setPassword(new Password(passwordEncoder.encode("nimeS321")));
        user4.setUserType(UserType.REGULAR);

        var user5 = new UserClass();
        user5.setUsername(new Username("test"));
        user5.setPassword(new Password(passwordEncoder.encode("password")));
        user5.setUserType(UserType.REGULAR);


        var poll1 = new Poll();
        poll1.setIsPrivate(false);
        poll1.setQuestion("Is pineapple on pizza allowed?");
        //TODO maybe change this to be hardcoded again? This is just for easily testing to dweet.io
        poll1.setVotingStart(Timestamp.valueOf(LocalDateTime.now().plusSeconds(5)));
        poll1.setVotingEnd(Timestamp.valueOf(LocalDateTime.now().plusSeconds(20)));
        poll1.addCreator(user1);

        var poll2 = new Poll ();
        poll2.setIsPrivate(true);
        poll2.setQuestion("Are dogs better than cats?");
        poll2.setVotingStart(Timestamp.valueOf("2021-12-12 12:00:00"));
        poll2.setVotingEnd(Timestamp.valueOf("2021-12-12 12:30:00"));
        poll2.addCreator(user2);

        var poll3 = new Poll ();
        poll3.setIsPrivate(false);
        poll3.setQuestion("Is there a war in Bas Sing Se?");
        poll3.setVotingStart(Timestamp.valueOf(LocalDateTime.now().plusYears(4)));
        poll3.setVotingEnd(Timestamp.valueOf("2022-01-01 00:00:00"));

        user1.voteOnPoll(poll1, "yes", VoteType.USER);
        user2.voteOnPoll(poll1, "no", VoteType.ANONYMOUS);

        user1.voteOnPoll(poll2, "yes", VoteType.USER);
        user2.voteOnPoll(poll2, "yes", VoteType.USER);
        user2.voteOnPoll(poll3, "yes", VoteType.USER);
        user3.voteOnPoll(poll2, "yes", VoteType.USER);
        user4.voteOnPoll(poll2, "no", VoteType.USER);

        var guestVote = new Vote();
        guestVote.setVoteType(VoteType.GUEST);
        guestVote.setOptionChosen("no");
        guestVote.addPoll(poll2);

        userDAO.save(user1);
        userDAO.save(user2);
        userDAO.save(user3);
        userDAO.save(user4);
        userDAO.save(user5);

        userDAO.findAll().forEach(user ->
                log.info(String.format("Created user %s ", user.getUserStringWithoutPollsAndVotes())));

        pollDAO.save(poll1);
        pollDAO.save(poll2);
        pollDAO.save(poll3);

        pollDAO.findAll().forEach(poll ->
                log.info(String.format("Created poll: %s", poll.toString())));

    }
}
