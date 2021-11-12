package no.hvl.dat250.jpa.basicexample.dto;

import lombok.Data;
import no.hvl.dat250.jpa.basicexample.VoteType;

import java.util.UUID;

@Data
public class VoteDTO {
    private UUID id;
    private String optionChosen;
    private VoteType voteType;
    private UUID voterId;
    private UUID pollId;

    public VoteDTO(UUID id, String optionChosen, VoteType voteType, UUID voterId, UUID pollId) {
        this.id = id;
        this.optionChosen = optionChosen;
        this.voteType = voteType;
        this.voterId = voterId;
        this.pollId = pollId;
    }

    public VoteDTO() {
    }

}
