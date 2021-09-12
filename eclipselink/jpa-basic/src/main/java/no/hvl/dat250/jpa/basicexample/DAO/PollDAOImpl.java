package no.hvl.dat250.jpa.basicexample.DAO;

import no.hvl.dat250.jpa.basicexample.Poll;
import no.hvl.dat250.jpa.basicexample.Vote;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

public class PollDAOImpl implements PollDAO{

    EntityManager em;

    public PollDAOImpl(EntityManager em){
        this.em = em;
    }

    @Override
    public Optional<Poll> getPollById(Long id) {
        try{
            Query q = em.createQuery("select p from Poll p where p.id=:id");
            q.setParameter("id", id);
            Poll poll = (Poll) q.getSingleResult();
            return Optional.of(poll);
        }catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Poll> getAllPolls() {
        return em.createQuery("select p from Poll p").getResultList();
    }

    @Override
    public void savePoll(Poll poll) {
        em.getTransaction().begin();
        em.persist(poll);
        em.getTransaction().commit();
    }

    @Override
    public void updatePoll(Long id, Poll updatedPoll) {
        Optional<Poll> pollMaybe = getPollById(id);
        if(pollMaybe.isEmpty()){
            return;
        }
        Poll poll = pollMaybe.get();
        poll.setQuestion(updatedPoll.getQuestion());
        poll.setVotingStart(updatedPoll.getVotingStart());
        poll.setVotingEnd(updatedPoll.getVotingEnd());
        poll.setIsPrivate(updatedPoll.getIsPrivate());
        poll.setCode(updatedPoll.getCode());
        poll.setCreator(updatedPoll.getCreator());
        poll.setVotes(updatedPoll.getVotes());
        savePoll(poll);
    }

    @Override
    public void deletePoll(Long id) {
        Optional<Poll> pollMaybe = getPollById(id);
        if(pollMaybe.isEmpty()){
            return;
        }
        em.getTransaction().begin();
        em.createQuery("delete from Poll where id=:id").setParameter("id", id).executeUpdate();
        em.getTransaction().commit();
    }

    @Override
    public List<Poll> getAllPublicPolls() {
        return em.createQuery("select p from Poll p where p.isPrivate=false").getResultList();
    }

    @Override
    public Optional<List<Vote>> getAllVotesFromPollById(Long id) {
        try{
            Query q = em.createQuery("select p.votes from Poll p where p.id=:id");
            q.setParameter("id", id);
            List<Vote> votes = q.getResultList();
            return Optional.of(votes);
        }catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
