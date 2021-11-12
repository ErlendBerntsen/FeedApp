package no.hvl.dat250.jpa.basicexample.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
public class PollDTO {
    private UUID id;
    private String question;
    private Timestamp votingStart;
    private Timestamp votingEnd;
    private Boolean isPrivate;
    private Integer code;
    private UUID creatorId;
    private List<UUID> votesId;

    public PollDTO(){}

    public PollDTO(UUID id, String question, Timestamp votingStart, Timestamp votingEnd, Boolean isPrivate, Integer code, UUID creatorId, List<UUID> votesId) {
        this.id = id;
        this.question = question;
        this.votingStart = votingStart;
        this.votingEnd = votingEnd;
        this.isPrivate = isPrivate;
        this.code = code;
        this.creatorId = creatorId;
        this.votesId = votesId;
    }

}
