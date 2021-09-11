package no.hvl.dat250.jpa.basicexample;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
public class UserClass {
    @Id
    @GeneratedValue
    Long id;

    String username;
    String password;

    @Enumerated(value = EnumType.STRING)
    UserType userType;

    @OneToMany(mappedBy = "creator")
    List<Poll> createdPolls = new ArrayList<>();

    @OneToMany(mappedBy = "voter")
    List<Vote> votes = new ArrayList<>();

    public UserClass(){}

    public Vote voteOnPoll(Poll poll, String option, VoteType voteType){
        Vote vote = new Vote();
        vote.setOptionChosen(option);
        vote.addVoter(this);
        vote.setVoteType(voteType);
        vote.addPoll(poll);
        return vote;
    }

    public String getUserStringWithoutPollsAndVotes(){
        return  ("id: " + id +
                ", username: " + username +
                ", password: " + password +
                ", usertype: " + userType);
    }


    @Override
    public String toString(){
        return ("id: " + id +
                ", username: " + username +
                ", password: " + password +
                ", usertype: " + userType +
                ", createdpolls: " + createdPolls.toString() +
                ", votes: " + votes.toString());

    }

    @Override
    public int hashCode(){
        return Objects.hash(username);
    }
}
