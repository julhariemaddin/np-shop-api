package com.ecommerce.np_shop.repo;

import com.ecommerce.np_shop.entity.Account;
import com.ecommerce.np_shop.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query("select distinct a from Account a join a.roles r WHERE r.name = :name")
    Set<Account> findAllByRoleName(@Param("name") String name);

    Role getRoleByName(String name);

    Role findByName(String name);
}
