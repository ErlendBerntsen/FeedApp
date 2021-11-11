package no.hvl.dat250.jpa.basicexample.dto;

import lombok.Data;
import no.hvl.dat250.jpa.basicexample.VoteType;
import no.hvl.dat250.jpa.basicexample.entities.Vote;

import java.util.UUID;

@Data
public class VoteDTO {
    private Long id;
    private String optionChosen;
    private VoteType voteType;
    private Long voterId;
    private UUID pollId;

    public VoteDTO(Long id, String optionChosen, VoteType voteType, Long voterId, UUID pollId) {
        this.id = id;
        this.optionChosen = optionChosen;
        this.voteType = voteType;
        this.voterId = voterId;
        this.pollId = pollId;
    }

    public VoteDTO() {
    }

    public Vote convertToEntity(){
        Vote vote = new Vote();
        vote.setOptionChosen(this.optionChosen);
        vote.setVoteType(this.voteType);
        return vote;
    }
}
