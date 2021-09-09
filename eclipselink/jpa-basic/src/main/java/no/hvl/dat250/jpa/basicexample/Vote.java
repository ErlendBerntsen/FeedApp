package no.hvl.dat250.jpa.basicexample;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
public class Vote {
    @Id
    @GeneratedValue
    Long  id;

    String optionChosen;
    Boolean guestVote = false;
    Boolean anonVote = false;

    @ManyToOne
    UserClass voter;

    @ManyToOne
    Poll poll;

    public Vote(){
    }

    public void addVoter(UserClass user){
        this.voter = user;
        user.getVotes().add(this);
    }

    public void addPoll(Poll poll){
        this.poll = poll;
        poll.getVotes().add(this);
    }

    @Override
    public String toString(){
        return ("id: " + id +
                ", optionChosen: " + optionChosen +
                ", guestVote: " + guestVote +
                ", anonvote: " + anonVote +
                ", voter: " + getVoterString());
    }

    private String getVoterString(){
        if(voter == null) return "";
        return voter.getUserStringWithoutPollsAndVotes();
    }

    @Override
    public int hashCode(){
        return Objects.hash(optionChosen);
    }
}
