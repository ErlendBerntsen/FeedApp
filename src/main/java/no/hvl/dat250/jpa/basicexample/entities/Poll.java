package no.hvl.dat250.jpa.basicexample.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
public class Poll {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name="UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    private String question;

    /*
        String format for Timestamp: "yyyy-mm-dd hh:mm:ss.f..."
        The fractional portion of timestamp constants (.f...) can be omitted.
        For example: Timestamp timestamp = Timestamp.valueOf("2020-09-20 12:00:00")
     */
    private Timestamp votingStart;
    private Timestamp votingEnd;
    private Boolean isPrivate;
    private Integer code;


    @ManyToOne(cascade = CascadeType.PERSIST)
    private UserClass creator;


    @OneToMany (mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @Transient
    @JsonIgnore
    Random random = new Random();


    public Poll(){
        generateCode();
    }

    private void generateCode(){
        var upperbound = 1000000;
        var lowerbound = 100000;
        setCode(random.nextInt(upperbound-lowerbound)+lowerbound);
    }

    public boolean isPollOpenForVoting(){
        var currentTime = Timestamp.valueOf(LocalDateTime.now());
        return currentTime.after(votingStart) && (votingEnd == null || currentTime.before(votingEnd));
    }

    public Integer getYesVotes(){
        var yesVotes = 0;
        for(Vote vote : votes){
            if("yes".equalsIgnoreCase(vote.getOptionChosen())){
                yesVotes++;
            }
        }
        return yesVotes;
    }

    public Integer getNoVotes(){
        var noVotes = 0;
        for(Vote vote : votes){
            if("no".equalsIgnoreCase(vote.getOptionChosen())){
                noVotes++;
            }
        }
        return noVotes;
    }

    public void addCreator(UserClass user){
        if(user == null){
            return;
        }
        this.creator = user;
        user.getCreatedPolls().add(this);
    }

    public void removeCreator(){
        if(this.creator == null)return;
        this.creator.getCreatedPolls().remove(this);
        setCreator(null);
    }


    @Override
    public String toString(){
        return ("id: " + id +
                ", question: " + question +
                ", votingStart: " + votingStart +
                ", votingEnd: " + votingEnd +
                ", isPrivate: " + isPrivate +
                ", code: " + code +
                ", creator: " + getCreatorString());
    }

    private String getCreatorString(){
        if(creator == null)return "";
        return creator.getUserStringWithoutPollsAndVotes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var poll = (Poll) o;
        return id.equals(poll.id)
                && Objects.equals(question, poll.question)
                && Objects.equals(votingStart, poll.votingStart)
                && Objects.equals(votingEnd, poll.votingEnd)
                && Objects.equals(isPrivate, poll.isPrivate)
                && Objects.equals(code, poll.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, question, votingStart, votingEnd, isPrivate, code);
    }
}
