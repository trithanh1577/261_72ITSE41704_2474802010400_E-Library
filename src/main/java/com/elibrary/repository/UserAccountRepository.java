package com.elibrary.repository;

import com.elibrary.domain.UserAccount;
import com.elibrary.domain.Role;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserAccount u where u.username = :username")
    Optional<UserAccount> findByUsernameForUpdate(@Param("username") String username);

    Optional<UserAccount> findByUsernameKey(String usernameKey);
    Optional<UserAccount> findByEmailIgnoreCase(String email);
    boolean existsByUsername(String username);
    boolean existsByUsernameKey(String usernameKey);
    boolean existsByEmailIgnoreCase(String email);

    List<UserAccount> findAllByRoleOrderByDisplayNameAsc(Role role);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserAccount u where u.id = :id")
    Optional<UserAccount> findByIdForUpdate(@Param("id") Long id);
}
