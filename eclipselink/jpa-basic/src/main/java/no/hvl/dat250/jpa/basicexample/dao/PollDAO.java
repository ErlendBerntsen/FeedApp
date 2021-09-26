package no.hvl.dat250.jpa.basicexample.dao;

import no.hvl.dat250.jpa.basicexample.entities.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollDAO extends JpaRepository<Poll, Long> {

    List<Poll> findByIsPrivate(boolean isPrivate);
    //Custom operations
    //Optional<List<Vote>> getAllVotesFromPollById(Long id);
}
