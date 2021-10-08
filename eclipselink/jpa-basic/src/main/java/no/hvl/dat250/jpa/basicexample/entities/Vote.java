package no.hvl.dat250.jpa.basicexample.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import lombok.Data;
import no.hvl.dat250.jpa.basicexample.VoteType;

import javax.persistence.*;

@Entity
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = ObjectIdResolver.class)
public class Vote {
    @Id
    @GeneratedValue
    Long  id;

    String optionChosen;

    @Enumerated(EnumType.STRING)
    VoteType voteType;

    @ManyToOne(cascade = CascadeType.PERSIST)
    UserClass voter;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
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

    public void removeVoter(){
        this.voter.getVotes().remove(this);
        setVoter(null);
    }

    public void removePoll(){
        this.poll.getVotes().remove(this);
        setPoll(null);
    }

    @Override
    public String toString(){
        return ("id: " + id +
                ", optionChosen: " + optionChosen +
                ", voteType: " + voteType +
                ", voter: " + getVoterString() +
                ", poll: " + poll.toString());
    }

    private String getVoterString(){
        if(voter == null) return "";
        return voter.getUserStringWithoutPollsAndVotes();
    }

    @Override
    public int hashCode(){
        return getClass().hashCode();
    }
}
