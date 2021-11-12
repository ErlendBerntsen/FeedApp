package no.hvl.dat250.jpa.basicexample.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import no.hvl.dat250.jpa.basicexample.UserType;
import no.hvl.dat250.jpa.basicexample.VoteType;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static no.hvl.dat250.jpa.basicexample.UserType.*;

@Entity
@Data
public class UserClass {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name="UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

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
        var vote = new Vote();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var userClass = (UserClass) o;
        return id.equals(userClass.id)
                && Objects.equals(username, userClass.username)
                && Objects.equals(password, userClass.password)
                && userType == userClass.userType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, userType);
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
