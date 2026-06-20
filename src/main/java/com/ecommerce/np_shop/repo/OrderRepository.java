package com.ecommerce.np_shop.repo;

import com.ecommerce.np_shop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIdAndAccountId(UUID orderId,UUID accountId);

    Page<Order> findByAccountId(UUID userId , Pageable pageable);

    @EntityGraph(attributePaths = "payment")
    List<Order> findByStatusAndExpiredAtBefore(String status, Instant now);
    @EntityGraph(attributePaths = "payment")
    Order findByPaymentPaymentId(String paypalId);
}
