package no.hvl.dat250.jpa.basicexample.DAO;

import no.hvl.dat250.jpa.basicexample.UserClass;

import java.util.List;
import java.util.Optional;

public interface UserDAO{

    //Default operations
    Optional<UserClass> getUserById(Long id);
    List<UserClass> getAllUsers();
    void saveUser(UserClass user);
    void updateUser(Long id, UserClass user);
    void deleteUser(Long id);

    //Custom operations
    Optional<UserClass> getUserByUsernameAndPassword(String username, String password);
}
