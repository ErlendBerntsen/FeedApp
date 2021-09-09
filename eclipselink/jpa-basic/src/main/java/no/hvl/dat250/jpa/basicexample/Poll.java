package no.hvl.dat250.jpa.basicexample;

import jdk.jfr.MetadataDefinition;
import lombok.Data;

import javax.persistence.*;
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

    //TODO make this temporal and choose a java date & time datastructure
    String votingStart;
    String votingEnd;

    Boolean isPrivate;
    //TODO Make this a unique generated value (only if isPrivate is true)
    Long code;

    @ManyToOne
    UserClass creator;

    @OneToMany (orphanRemoval = true)
    List<Vote> votes = new ArrayList<>();

    public Poll(){}

    public void addCreator(UserClass user){
        this.creator = user;
        user.getCreatedPolls().add(this);
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
