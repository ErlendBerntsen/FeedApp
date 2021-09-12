package no.hvl.dat250.jpa.basicexample.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
    Integer code;

    @ManyToOne(cascade = CascadeType.PERSIST)
    UserClass creator;

    @OneToMany (mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Vote> votes = new ArrayList<>();

    public Poll(){
        generateCode();
    }

    private void generateCode(){
        Random random = new Random();
        int upperbound = 1000000;
        int lowerbound = 100000;
        setCode(random.nextInt(upperbound-lowerbound)+lowerbound);
    }

    public void addCreator(UserClass user){
        this.creator = user;
        user.getCreatedPolls().add(this);
    }

    public void removeCreator(){
        assert(this.creator != null);
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
    public int hashCode(){
        return Objects.hash(question);
    }
}
