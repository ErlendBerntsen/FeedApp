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
import java.util.UUID;

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

    public Optional<UserClass> getUser(UUID id){
        return userDAO.findById(id);
    }

    public Optional<UserClass> getUserByUsername(Username username){
        return userDAO.findByUsername(username);
    }

    public UserClass createUser(UserClass user){
        var encrypted = new Password(passwordEncoder.encode(user.getPassword().getPassword()));
        user.setPassword(encrypted);
        return userDAO.save(user);
    }

    public Optional<UserClass> updateUser(UserClass userToUpdate, UserDTO updatedUser){
        if (updatedUser.getUsername() != null){
            //Abort the update if trying to change username to an already taken username
            if( getUserByUsername(updatedUser.getUsername()).isPresent()){
                return Optional.empty();
            }
            userToUpdate.setUsername(updatedUser.getUsername());
        }

        //If the update is changing password then encrypt it also
        if(updatedUser.getPassword() != null){
            var encrypted = new Password(passwordEncoder.encode(updatedUser.getPassword().getPassword()));
            userToUpdate.setPassword(encrypted);
        }

        if(updatedUser.getUserType() != null){
            userToUpdate.setUserType(updatedUser.getUserType());
        }
        return Optional.of(userToUpdate);
    }

    public void deleteUser(UUID id){
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
