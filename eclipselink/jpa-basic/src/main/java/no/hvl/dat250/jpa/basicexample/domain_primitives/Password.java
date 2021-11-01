package no.hvl.dat250.jpa.basicexample.domain_primitives;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.*;

public class Password {

    private final String password;

    public Password(final String password){
        notNull(password);
        notBlank(password);
        isTrue(password.length() >= 8 && password.length() <= 128);
        this.password = password;
    }

    @JsonValue
    public String toJson(){
        return this.password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password1 = (Password) o;
        return password.equals(password1.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password);
    }
}
