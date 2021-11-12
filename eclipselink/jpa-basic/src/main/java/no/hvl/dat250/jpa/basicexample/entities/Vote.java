package no.hvl.dat250.jpa.basicexample.entities;

import lombok.Data;
import no.hvl.dat250.jpa.basicexample.VoteType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
public class Vote {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name="UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String optionChosen;

    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private UserClass voter;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Poll poll;

    public Vote(){
    }

    public void addVoter(UserClass user){
        if(user == null){
            return;
        }
        this.voter = user;
        user.getVotes().add(this);
    }

    public void addPoll(Poll poll){
        if(poll==null){
            return;
        }
        this.poll = poll;
        poll.getVotes().add(this);
    }

    public void removeVoter(){
        if(this.voter == null){
            return;
        }
        this.voter.getVotes().remove(this);
        setVoter(null);
    }

    public void removePoll(){
        this.poll.getVotes().remove(this);
        setPoll(null);
    }

    private String getVoterString(){
        if(voter == null) return "";
        return voter.getUserStringWithoutPollsAndVotes();
    }

    @Override
    public String toString(){
        return ("id: " + id +
                ", optionChosen: " + optionChosen +
                ", voteType: " + voteType +
                ", voter: " + getVoterString() +
                ", poll: " + poll.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var vote = (Vote) o;
        return id.equals(vote.id)
                && Objects.equals(optionChosen, vote.optionChosen)
                && voteType == vote.voteType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, optionChosen, voteType);
    }
}
