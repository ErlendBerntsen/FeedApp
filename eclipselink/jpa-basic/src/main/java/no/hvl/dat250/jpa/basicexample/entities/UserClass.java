package no.hvl.dat250.jpa.basicexample.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import lombok.Data;
import no.hvl.dat250.jpa.basicexample.UserType;
import no.hvl.dat250.jpa.basicexample.VoteType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = ObjectIdResolver.class)
public class UserClass {
    @Id
    @GeneratedValue
    Long id;

    String username;
    String password;

    @Enumerated(value = EnumType.STRING)
    UserType userType;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.PERSIST)
    List<Poll> createdPolls = new ArrayList<>();

    @JsonIgnore//TODO:??????????????????????????????????????????????????????????????
    @OneToMany(mappedBy = "voter", cascade = CascadeType.PERSIST)
    List<Vote> votes = new ArrayList<>();

    public UserClass(){}

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
                ", username: " + username +
                ", password: " + password +
                ", userType: " + userType);
    }


    @Override
    public String toString(){
        return ("id: " + id +
                ", username: " + username +
                ", password: " + password +
                ", userType: " + userType +
                ", createdPolls: " + createdPolls.toString() +
                ", votes: " + votes.toString());

    }

    @Override
    public int hashCode(){
        return Objects.hash(username);
    }
}
