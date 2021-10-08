package no.hvl.dat250.jpa.basicexample.entities;

import lombok.Data;
import no.hvl.dat250.jpa.basicexample.VoteType;
import no.hvl.dat250.jpa.basicexample.dto.VoteDTO;

import javax.persistence.*;

@Entity
@Data
public class Vote {
    @Id
    @GeneratedValue
    private Long  id;

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

    public VoteDTO convertToDTO(){
        var voter = this.voter == null? null : this.voter.getId();
        var poll = this.poll == null? null : this.poll.getId();
        return new VoteDTO(this.id,
                this.optionChosen,
                this.voteType,
                voter,
                poll);
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
