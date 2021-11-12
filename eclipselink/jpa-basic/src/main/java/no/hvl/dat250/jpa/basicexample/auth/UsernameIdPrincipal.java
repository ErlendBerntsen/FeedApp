package no.hvl.dat250.jpa.basicexample.auth;


import lombok.Data;

import java.security.Principal;
import java.util.UUID;

@Data
public class UsernameIdPrincipal implements Principal {

    private final UUID id;
    private final String username;

    public UsernameIdPrincipal(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public String toString() {
        return "UsernameIdPrincipal{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
