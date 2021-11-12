package no.hvl.dat250.jpa.basicexample.dao;

import no.hvl.dat250.jpa.basicexample.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoteDAO extends JpaRepository<Vote, UUID> {

}
