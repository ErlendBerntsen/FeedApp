package no.hvl.dat250.jpa.basicexample.dto;

import lombok.Data;
import no.hvl.dat250.jpa.basicexample.entities.Poll;

import java.sql.Timestamp;
import java.util.List;

@Data
public class PollDTO {
    private Long id;
    private String question;
    private Timestamp votingStart;
    private Timestamp votingEnd;
    private Boolean isPrivate;
    private Integer code;
    private Long creatorId;
    private List<Long> votesId;

    public PollDTO(Long id, String question, Timestamp votingStart, Timestamp votingEnd, Boolean isPrivate, Integer code, Long creatorId, List<Long> votesId) {
        this.id = id;
        this.question = question;
        this.votingStart = votingStart;
        this.votingEnd = votingEnd;
        this.isPrivate = isPrivate;
        this.code = code;
        this.creatorId = creatorId;
        this.votesId = votesId;
    }

    public Poll convertToEntity(){
        Poll poll = new Poll();
        poll.setQuestion(this.question);
        poll.setVotingStart(this.votingStart);
        poll.setVotingEnd(this.votingEnd);
        poll.setIsPrivate(this.isPrivate);
        return poll;
    }
}
