package no.hvl.dat250.jpa.basicexample.DAO;

import no.hvl.dat250.jpa.basicexample.Poll;
import no.hvl.dat250.jpa.basicexample.Vote;

import java.util.List;
import java.util.Optional;

public interface VoteDAO {
    //Default operations
    Optional<Vote> getVoteById(Long id);
    List<Vote> getAllVotes();
    void saveVote(Vote vote);
    void updateVote(Long id, Vote vote);
    void deleteVote(Long id);

    //Custom operations
    Optional<Poll> getPollFromVoteId(Long id);
}
