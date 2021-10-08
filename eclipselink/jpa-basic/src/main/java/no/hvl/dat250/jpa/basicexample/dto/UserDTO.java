package no.hvl.dat250.jpa.basicexample.dto;

import lombok.Data;
import no.hvl.dat250.jpa.basicexample.UserType;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private UserType userType;
    private List<Long> createdPollsId;
    private List<Long> votesId;

    public UserDTO(Long id, String username, String password, UserType userType, List<Long> createdPollsId, List<Long> votesId) {
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
