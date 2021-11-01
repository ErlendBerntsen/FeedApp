package no.hvl.dat250.jpa.basicexample.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import no.hvl.dat250.jpa.basicexample.UserType;
import no.hvl.dat250.jpa.basicexample.VoteType;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.dto.UserDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static no.hvl.dat250.jpa.basicexample.UserType.*;

@Entity
@Data
public class UserClass {
    @JsonIgnore
    @Id
    @GeneratedValue
    private Long id;


    @Convert(converter = UsernameConverter.class)
    private Username username;

    @Convert(converter = PasswordConverter.class)
    private Password password;

    @Enumerated(value = EnumType.STRING)
    private UserType userType = REGULAR;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.PERSIST)
    private List<Poll> createdPolls = new ArrayList<>();


    @OneToMany(mappedBy = "voter", cascade = CascadeType.PERSIST)
    private List<Vote> votes = new ArrayList<>();

    public UserClass(){}

    public UserClass(Username username, Password password, UserType userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    public Vote voteOnPoll(Poll poll, String option, VoteType voteType){
        Vote vote = new Vote();
        vote.setOptionChosen(option);
        if(voteType.equals(VoteType.USER)){
            vote.addVoter(this);
        }
        vote.setVoteType(voteType);
        vote.addPoll(poll);
        return vote;
    }


    @JsonIgnore
    public String getUserStringWithoutPollsAndVotes(){
        return  ("id: " + id +
                ", username: " + username.getUsername() +
                ", password: " + password.getPassword() +
                ", userType: " + userType);
    }

    public UserDTO convertToDTO(){
        List<Long> createdPollsId = new ArrayList<>();
        createdPolls.forEach(poll -> createdPollsId.add(poll.getId()));
        List<Long> votesId = new ArrayList<>();
        votes.forEach(vote -> votesId.add(vote.getId()));
        return new UserDTO(this.id,
                this.username,
                this.password,
                this.userType,
                createdPollsId,
                votesId);
    }


    @Override
    public String toString(){
        return ("id: " + id +
                ", username: " + username.getUsername() +
                ", password: " + password.getPassword() +
                ", userType: " + userType +
                ", createdPolls: " + createdPolls.toString() +
                ", votes: " + votes.toString());

    }

    @Override
    public int hashCode(){
        return Objects.hash(username);
    }
}

class UsernameConverter implements AttributeConverter<Username, String>{

    @Override
    public String convertToDatabaseColumn(Username username) {
        return username.getUsername();
    }

    @Override
    public Username convertToEntityAttribute(String username) {
        return new Username(username);
    }
}

class PasswordConverter implements AttributeConverter<Password, String>{

    @Override
    public String convertToDatabaseColumn(Password password) {
        return password.getPassword();
    }

    @Override
    public Password convertToEntityAttribute(String password) {
        return new Password(password);
    }
}
