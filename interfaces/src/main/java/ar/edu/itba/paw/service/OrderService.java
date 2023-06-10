package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderStatus;
import ar.edu.itba.paw.util.PaginatedResult;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    Order createDelivery(long restaurantId, String name, String email, String address, List<OrderItem> items);

    Order createDineIn(long restaurantId, String name, String email, int tableNumber, List<OrderItem> items);

    Order createTakeAway(long restaurantId, String name, String email, List<OrderItem> items);

    OrderItem createOrderItem(long restaurantId, long productId, int i, int quantity, String comment);

    Optional<Order> getById(long orderId);

    PaginatedResult<Order> getByUser(long userId, int pageNumber, int pageSize, boolean onlyInProgress, boolean descending);

    PaginatedResult<Order> getByRestaurant(long restaurantId, int pageNumber, int pageSize, OrderStatus orderStatus, boolean descending);

    Order markAsConfirmed(long orderId);

    Order markAsReady(long orderId);

    Order markAsDelivered(long orderId);

    Order markAsCancelled(long orderId);

    /**
     * Warning: this method forcedly modifies an order's status. No checks are performed and no
     * notifications are sent, so this method should only be used on extreme circumstances.
     */
    void setOrderStatus(long orderId, OrderStatus orderStatus);

    void updateAddress(long orderId, String address);

    void updateTableNumber(long orderId, int tableNumber);
}
