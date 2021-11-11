package no.hvl.dat250.jpa.basicexample.dto;

import lombok.Data;
import no.hvl.dat250.jpa.basicexample.UserType;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Password;
import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;

import java.util.List;
import java.util.UUID;

@Data
public class UserDTO {
    private final Long id;
    private final Username username;
    private final UserType userType;
    private List<UUID> createdPollsId;
    private List<Long> votesId;

    public UserDTO(Long id, Username username, UserType userType, List<UUID> createdPollsId, List<Long> votesId) {
        this.id = id;
        this.username = username;
        this.userType = userType;
        this.createdPollsId = createdPollsId;
        this.votesId = votesId;
    }

    public UserClass convertToEntity(){
        UserClass user = new UserClass();
        user.setUsername(this.username);
        user.setUserType(this.userType);
        return user;
    }
}
