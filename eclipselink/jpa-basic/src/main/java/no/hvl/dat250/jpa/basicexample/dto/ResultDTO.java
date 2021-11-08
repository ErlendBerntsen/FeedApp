package no.hvl.dat250.jpa.basicexample.dto;

import lombok.Data;

@Data
public class ResultDTO {
    Integer yesVotes;
    Integer noVotes;

    public ResultDTO(Integer yesVotes, Integer noVotes) {
        this.yesVotes = yesVotes;
        this.noVotes = noVotes;
    }

    public ResultDTO() {
    }
}
