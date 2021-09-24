package no.hvl.dat250.jpa.basicexample.services;

import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    public List<UserClass> getAllUsers(){
        return userDAO.findAll();
    }

    public Optional<UserClass> getUser(Long id){
        return userDAO.findById(id);
    }

    public UserClass createUser(UserClass user){
        return userDAO.save(user);
    }

    public UserClass updateUser(Long id, UserClass updatedUser){
        var user = getUser(id);
        if(user.isPresent()){
            var userToUpdate = user.get();
            userToUpdate.setUsername(updatedUser.getUsername());
            userToUpdate.setPassword(updatedUser.getPassword());
            userToUpdate.setUserType(updatedUser.getUserType());
            return userToUpdate;
        }else{
            return createUser(updatedUser);
        }
    }

    public void deleteUser(Long id){
        userDAO.deleteById(id);
    }


}
