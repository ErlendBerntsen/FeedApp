package no.hvl.dat250.jpa.basicexample;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Vote {
    @Id
    @GeneratedValue
    Long  id;

    String optionChosen;

    @Enumerated(EnumType.STRING)
    VoteType voteType;

    @ManyToOne
    UserClass voter;

    @ManyToOne(fetch = FetchType.LAZY)
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
                ", voteType: " + voteType +
                ", voter: " + getVoterString());
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
