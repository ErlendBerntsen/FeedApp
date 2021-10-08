package no.hvl.dat250.jpa.basicexample.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import no.hvl.dat250.jpa.basicexample.UserType;
import no.hvl.dat250.jpa.basicexample.VoteType;
import no.hvl.dat250.jpa.basicexample.dto.UserDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
public class UserClass {
    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;

    @Enumerated(value = EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.PERSIST)
    private List<Poll> createdPolls = new ArrayList<>();

    @JsonIgnore//TODO:??????????????????????????????????????????????????????????????
    @OneToMany(mappedBy = "voter", cascade = CascadeType.PERSIST)
    private List<Vote> votes = new ArrayList<>();

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
