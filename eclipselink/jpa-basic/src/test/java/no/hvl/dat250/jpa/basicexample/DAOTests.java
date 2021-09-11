package no.hvl.dat250.jpa.basicexample;

import no.hvl.dat250.jpa.basicexample.DAO.UserDAOImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class DAOTests {

    private static final String PERSISTENCE_UNIT_NAME = "votingsystem";
    private static EntityManagerFactory factory;
    private EntityManager em;
    private UserDAOImpl userDAO;
    private UserClass user;

    @Before
    public void setUp() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
        userDAO = new UserDAOImpl(em);
        userDAO.getAllUsers().forEach(user -> em.remove(user));
        user = new UserClass();
        user.setUsername("TestUser1");
        user.setPassword("123");
        user.setUserType(UserType.REGULAR);
    }

    @After
    public void cleanUp(){
        em.close();
    }

    @Test
    public void persistedUserShouldBeFoundInDatabase(){
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        UserClass user = (UserClass)em.createQuery("select u from UserClass u").getSingleResult();
        Optional<UserClass> userMaybe = userDAO.getUserById(user.getId());
        assertTrue(userMaybe.isPresent());
        assertEquals(user, userMaybe.get());
    }

    @Test
    public void nonPersistedUserShouldNotBeFoundInDatabase(){
        Optional<UserClass> userMaybe = userDAO.getUserById(-1L);
        assertFalse(userMaybe.isPresent());
    }

    @Test
    public void allUsersShouldBeFoundInDatabase(){
        UserClass user2 = new UserClass();
        user2.setUsername("TestUser2");
        user2.setPassword("456");
        user2.setUserType(UserType.ADMIN);
        em.getTransaction().begin();
        em.persist(user);
        em.persist(user2);
        em.getTransaction().commit();
        List<UserClass> users = userDAO.getAllUsers();
        System.out.println(users);
        assertEquals(2, users.size());
        assertTrue(users.contains(user));
        assertTrue(users.contains(user2));
    }

    @Test
    public void userShouldBeSavedInDatabase(){
        userDAO.saveUser(user);
        Optional<UserClass> userMaybe = userDAO.getUserById(user.getId());
        assertTrue(userMaybe.isPresent());
        assertEquals(user.getId(), userMaybe.get().getId());
    }

    @Test
    public void userShouldBeUpdatedInDatabase(){
        UserClass user2 = new UserClass();
        user2.setUsername("TestUser2");
        user2.setPassword("456");
        user2.setUserType(UserType.ADMIN);

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        userDAO.updateUser(user.getId(), user2);

        Optional<UserClass> userMaybe = userDAO.getUserById(user.getId());
        assertTrue(userMaybe.isPresent());

        UserClass updatedUser = userMaybe.get();
        assertEquals(user2.getUsername(), updatedUser.getUsername());
        assertEquals(user2.getPassword(), updatedUser.getPassword());
        assertEquals(user2.getUserType(), updatedUser.getUserType());
    }

    @Test
    public void userShouldBeDeletedInDatabase(){
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        assertTrue(userDAO.getUserById(user.getId()).isPresent());
        userDAO.deleteUser(user.getId());
        assertFalse(userDAO.getUserById(user.getId()).isPresent());
    }

    @Test
    public void userShouldBeFoundByUsernameAndPasswordInDatabase(){
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        Optional<UserClass> userMaybe = userDAO.getUserByUsernameAndPassword(user.getUsername(), user.getPassword());
        assertTrue(userMaybe.isPresent());
        assertEquals(user.getId(), userMaybe.get().getId());
    }

    @Test
    public void userShouldBeFoundWithWrongUsername(){
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        Optional<UserClass> userMaybe = userDAO.getUserByUsernameAndPassword("", user.getPassword());
        assertFalse(userMaybe.isPresent());
    }

    @Test
    public void userShouldBeFoundWithWrongPassword(){
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        Optional<UserClass> userMaybe = userDAO.getUserByUsernameAndPassword(user.getUsername(), "");
        assertFalse(userMaybe.isPresent());
    }

}
