package no.hvl.dat250.jpa.basicexample.dao;

import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import no.hvl.dat250.jpa.basicexample.VoteType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO{

    EntityManager em;

    public UserDAOImpl(EntityManager em){
        this.em = em;
    }

    @Override
    public Optional<UserClass> getUserById(Long id) {
        try{
            Query q = em.createQuery("select u from UserClass u where u.id=:id");
            q.setParameter("id", id);
            UserClass user = (UserClass) q.getSingleResult();
            return Optional.of(user);
        }catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<UserClass> getAllUsers() {
        return em.createQuery("select u from UserClass u").getResultList();
    }

    @Override
    public void saveUser(UserClass user) {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }

    @Override
    public void updateUser(Long id, UserClass updatedUser) {
        Optional<UserClass> userMaybe = getUserById(id);
        if(userMaybe.isEmpty()){
            return;
        }
        UserClass user = userMaybe.get();
        user.setUsername(updatedUser.getUsername());
        user.setPassword(updatedUser.getPassword());
        user.setUserType(updatedUser.getUserType());
    }

    @Override
    public void deleteUser(Long id) {

        Optional<UserClass> userMaybe = getUserById(id);
        if(userMaybe.isEmpty()){
            return;
        }

        em.getTransaction().begin();
        userMaybe.get().getCreatedPolls().forEach(poll -> poll.setCreator(null));
        userMaybe.get().getVotes().forEach(vote -> {
            vote.setVoter(null);
            vote.setVoteType(VoteType.GUEST);
        });
        em.remove(userMaybe.get());
        em.getTransaction().commit();
    }

    @Override
    public Optional<UserClass> getUserByUsernameAndPassword(String username, String password) {
        try{
            Query q = em.createQuery("select u from UserClass u where u.username =:username and u.password=:password");
            q.setParameter("username", username);
            UserClass user = (UserClass) q.setParameter("password", password).getSingleResult();
            return Optional.of(user);
        }
        catch (NoResultException e){

            return Optional.empty();
        }
    }
}
