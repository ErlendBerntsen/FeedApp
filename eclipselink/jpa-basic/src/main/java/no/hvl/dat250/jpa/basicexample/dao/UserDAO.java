package no.hvl.dat250.jpa.basicexample.dao;

import no.hvl.dat250.jpa.basicexample.domain_primitives.Username;
import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface UserDAO extends JpaRepository<UserClass, UUID> {
    Optional<UserClass> findByUsername(Username username);
}
