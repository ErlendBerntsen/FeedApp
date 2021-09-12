package no.hvl.dat250.jpa.basicexample.DAO;

import no.hvl.dat250.jpa.basicexample.UserClass;

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
//        em.getTransaction().begin();
//        Query query = em.createQuery("update UserClass u set u.username=:username," +
//                "u.password=:password," +
//                "u.userType=:usertype," +
//                "u.createdPolls=:createdpolls," +
//                "u.votes=:votes");
//        query.setParameter("username", updatedUser.getUsername());
//        query.setParameter("password", updatedUser.getPassword());
//        query.setParameter("usertype", updatedUser.getUserType());
//        query.setParameter("createdpolls", updatedUser.getCreatedPolls());
//        query.setParameter("votes", updatedUser.getVotes());
//        query.executeUpdate();
//        em.getTransaction().commit();
        UserClass user = userMaybe.get();
        user.setUsername(updatedUser.getUsername());
        user.setPassword(updatedUser.getPassword());
        user.setUserType(updatedUser.getUserType());
        user.setCreatedPolls(updatedUser.getCreatedPolls());
        user.setVotes(updatedUser.getVotes());
        saveUser(user);
    }

    @Override
    public void deleteUser(Long id) {
        Optional<UserClass> userMaybe = getUserById(id);
        if(userMaybe.isEmpty()){
            return;
        }
        em.getTransaction().begin();
        em.createQuery("delete from UserClass where id=:id").setParameter("id", id).executeUpdate();
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
