package com.ecommerce.np_shop.repo;

import com.ecommerce.np_shop.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findAllByUsername(String username);
    boolean existsByUsername(String username);
}
