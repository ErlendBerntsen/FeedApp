package no.hvl.dat250.jpa.basicexample.domain_primitives;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.*;

public class Username {

    private final String username;

    public Username(final String username){
        notNull(username);
        notBlank(username);
        isTrue(username.length() >= 3 && username.length() <= 32);
        this.username = username;
    }

    @JsonValue
    public String toJson(){
        return this.username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var username1 = (Username) o;
        return username.equals(username1.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
