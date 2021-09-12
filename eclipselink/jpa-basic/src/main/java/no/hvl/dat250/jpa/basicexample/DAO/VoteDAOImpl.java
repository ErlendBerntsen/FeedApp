package no.hvl.dat250.jpa.basicexample.DAO;

import no.hvl.dat250.jpa.basicexample.Poll;
import no.hvl.dat250.jpa.basicexample.Vote;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

public class VoteDAOImpl implements VoteDAO{

    EntityManager em;

    public VoteDAOImpl(EntityManager em){
        this.em = em;
    }

    @Override
    public Optional<Vote> getVoteById(Long id) {
        try{
            Query q = em.createQuery("select v from Vote v where v.id=:id");
            q.setParameter("id", id);
            Vote vote = (Vote) q.getSingleResult();
            return Optional.of(vote);
        }catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Vote> getAllVotes() {
        return em.createQuery("select v from Vote v").getResultList();
    }

    @Override
    public void saveVote(Vote vote) {
        em.getTransaction().begin();
        em.persist(vote);
        em.getTransaction().commit();
    }

    @Override
    public void updateVote(Long id, Vote updatedVote) {
        Optional<Vote> voteMaybe = getVoteById(id);
        if(voteMaybe.isEmpty()){
            return;
        }
        Vote vote = voteMaybe.get();
        vote.setOptionChosen(updatedVote.getOptionChosen());
        vote.setVoteType(updatedVote.getVoteType());
        //Being allowed to update the voter or the poll in a vote does not make any sense
        saveVote(vote);
    }

    @Override
    public void deleteVote(Long id) {
        Optional<Vote> voteMaybe = getVoteById(id);
        if(voteMaybe.isEmpty()){
            return;
        }
        em.getTransaction().begin();
        em.createQuery("delete from Vote where id=:id").setParameter("id", id).executeUpdate();
        em.getTransaction().commit();
    }

    @Override
    public Optional<Poll> getPollFromVoteId(Long id) {
        try{
            Query q = em.createQuery("select v.poll from Vote v where v.id=:id");
            q.setParameter("id", id);
            Poll poll = (Poll) q.getSingleResult();
            return Optional.of(poll);
        }catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
