package no.hvl.dat250.jpa.basicexample.DAO;

import no.hvl.dat250.jpa.basicexample.Poll;
import no.hvl.dat250.jpa.basicexample.Vote;

import java.util.List;
import java.util.Optional;

public interface PollDAO {

    //Default operations
    Optional<Poll> getPollById(Long id);
    List<Poll> getAllPolls();
    void savePoll(Poll poll);
    void updatePoll(Long id, Poll poll);
    void deletePoll(Long id);

    //Custom operations
    List<Poll> getAllPublicPolls();
    Optional<List<Vote>> getAllVotesFromPollById(Long id);
}
