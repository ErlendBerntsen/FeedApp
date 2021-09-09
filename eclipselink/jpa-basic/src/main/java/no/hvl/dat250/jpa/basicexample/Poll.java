package no.hvl.dat250.jpa.basicexample;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
public class Poll {
    @Id
    @GeneratedValue
    Long id;
    String question;

    /*
        String format for Timestamp: "yyyy-mm-dd hh:mm:ss.f..."
        The fractional portion of timestamp constants (.f...) can be omitted.
        For example: Timestamp timestamp = Timestamp.valueOf("2020-09-20 12:00:00")
     */
    Timestamp votingStart;
    Timestamp votingEnd;

    Boolean isPrivate;

    //TODO Make this a unique generated value (only if isPrivate is true)
    Integer code;

    @ManyToOne
    UserClass creator;

    @OneToMany (orphanRemoval = true)
    List<Vote> votes = new ArrayList<>();

    public Poll(){}

    public void addCreator(UserClass user){
        this.creator = user;
        user.getCreatedPolls().add(this);
    }

    @PrePersist
    private void generateCode(){

    }

    @Override
    public String toString(){
        return ("id: " + id +
                ", question: " + question +
                ", votingstart: " + votingStart +
                ", votingend: " + votingEnd +
                ", isprivate: " + isPrivate +
                ", code: " + code +
                ", creator: " + getCreatorString());
    }

    private String getCreatorString(){
        if(creator == null)return "";
        return creator.getUserStringWithoutPollsAndVotes();
    }
    @Override
    public int hashCode(){
        return Objects.hash(question);
    }
}
