package ru.newrav1k.mirea.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.newrav1k.mirea.orderservice.model.entity.Order;
import ru.newrav1k.mirea.orderservice.model.enums.OrderStatus;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<OrderStatus> findStatusById(UUID orderId);

    @Modifying
    @Query("update Order o set o.status = :status where o.id = :orderId")
    void updateStatus(UUID orderId, OrderStatus status);

}