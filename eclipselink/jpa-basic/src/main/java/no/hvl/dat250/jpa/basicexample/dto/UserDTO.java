package no.hvl.dat250.jpa.basicexample.dto;

import lombok.Data;
import no.hvl.dat250.jpa.basicexample.UserType;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;

import java.util.List;
import java.util.UUID;

@Data
public class UserDTO {
    private final UUID id;
    private final Username username;
    private final Password password;
    private final UserType userType;
    private List<UUID> createdPollsId;
    private List<UUID> votesId;

    public UserDTO(UUID id, Username username, Password password, UserType userType, List<UUID> createdPollsId, List<UUID> votesId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.createdPollsId = createdPollsId;
        this.votesId = votesId;
    }

}
