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
    //TODO password should not be included
    private final Username username;
    private final Password password;
    private final UserType userType;
    private List<UUID> createdPollsId;
    private List<Long> votesId;

    public UserDTO(Long id, Username username, Password password, UserType userType, List<UUID> createdPollsId, List<Long> votesId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.createdPollsId = createdPollsId;
        this.votesId = votesId;
    }

    public UserClass convertToEntity(){
        UserClass user = new UserClass();
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setUserType(this.userType);
        return user;
    }
}
