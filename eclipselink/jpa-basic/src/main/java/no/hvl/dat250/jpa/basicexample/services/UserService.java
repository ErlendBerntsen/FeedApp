package no.hvl.dat250.jpa.basicexample.services;

import no.hvl.dat250.jpa.basicexample.VoteType;
import no.hvl.dat250.jpa.basicexample.dao.UserDAO;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.dto.UserDTO;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDAO userDAO, PasswordEncoder passwordEncoder){
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserClass> getAllUsers(){
        return userDAO.findAll();
    }

    public Optional<UserClass> getUser(Long id){
        return userDAO.findById(id);
    }

    public Optional<UserClass> getUserByUsername(Username username){
        return userDAO.findByUsername(username);
    }

    public UserClass createUser(UserClass user){
        //TODO maybe handle this somewhere else?
        Password encrypted = new Password(passwordEncoder.encode(user.getPassword().getPassword()));
        user.setPassword(encrypted);
        return userDAO.save(user);
    }

    public UserClass updateUser(Long id, UserDTO updatedUser){
        var user = getUser(id);
        if(user.isPresent()){
            var userToUpdate = user.get();
            userToUpdate.setUsername(updatedUser.getUsername());
            userToUpdate.setPassword(updatedUser.getPassword());
            userToUpdate.setUserType(updatedUser.getUserType());
            return userToUpdate;
        }else{
            return createUser(updatedUser.convertToEntity());
        }
    }

    public void deleteUser(Long id){
        var userMaybe = getUser(id);
        if(userMaybe.isPresent()){
            var user = userMaybe.get();
            user.getCreatedPolls().forEach(poll -> poll.setCreator(null));
            user.getVotes().forEach(vote -> {
                vote.setVoter(null);
                vote.setVoteType(VoteType.GUEST);
            });
            userDAO.deleteById(id);
        }
    }

}
