package com.Marketplace_Management.Order.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Marketplace_Management.Cart.Contracts.ICartService;
import com.Marketplace_Management.Cart.DTOs.Responses.CartResponse;
import com.Marketplace_Management.Order.Constants.OrderStatusEnum;
import com.Marketplace_Management.Order.Contracts.IOrderRepository;
import com.Marketplace_Management.Order.Contracts.IOrderService;
import com.Marketplace_Management.Order.DTOs.Commands.ListOrderCommand;
import com.Marketplace_Management.Order.DTOs.Commands.OrderItemCommand;
import com.Marketplace_Management.Order.DTOs.Commands.PlaceOrderCommand;
import com.Marketplace_Management.Order.DTOs.Responses.HistoryResponse;
import com.Marketplace_Management.Order.DTOs.Responses.OrderResponse;
import com.Marketplace_Management.Order.Events.OrderPlacedEvent;
import com.Marketplace_Management.Order.Models.Order;
import com.Marketplace_Management.Order.Models.OrderItem;
import com.Marketplace_Management.Order.Models.ProductSnapShot;
import com.Marketplace_Management.Shared.Configuration.RabbitMqQueues.OrderQueueConfig;
import com.Marketplace_Management.Shared.Contracts.IEventPublisher;
import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;
import com.Marketplace_Management.Shared.Events.EventOptions;
import com.Marketplace_Management.Shared.Security.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
public class OrderService implements IOrderService{
    private final IOrderRepository repository;
    private final IEventPublisher eventPublisher;
    private final ICartService cartService;

    public OrderService(IOrderRepository repository, IEventPublisher eventPublisher, ICartService cartService) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.cartService = cartService;
    }

    public PaginatedResponse<HistoryResponse> list(ListOrderCommand command) {
        return repository.findAll(command);
    }
    
    public PaginatedResponse<HistoryResponse> listByUser(ListOrderCommand command) {
        return repository.findByUserIdWithFilters(SecurityUtils.currentUserId(), command);
    }

    public OrderResponse findById(UUID id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse placeOrder(PlaceOrderCommand command) {
        CartResponse cartResponse = CartResponse.from(cartService.getByUserId(SecurityUtils.currentUserId()));
        
        if(cartResponse == null || cartResponse.getItems() == null || cartResponse.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart not found or empty");
        }

        List<OrderItemCommand> items = new ArrayList<>();

        for (var item : cartResponse.getItems()) {
            //ProductResponse product = productService.getProduct(item.getProductVariantId());

            //if(product.getStock() < item.getQuantity()) {
            //    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product " + product.getName() + " does not have enough stock");
            //}

            items.add(new OrderItemCommand(
                item.getProductVariantId(),
                item.getQuantity(),
                item.getProductName(),
                item.getProductPrice(),
                item.getProductCode(),
                item.getProductDescription(),
                item.getProductImage()
            ));
        }

        command.setItems(items);

        Order order = new Order(
            null, 
            SecurityUtils.currentUserId(), 
            OrderStatusEnum.PENDING.getValue(),
            command.getItems().stream().map(item -> new OrderItem(
                null,
                item.getProductId(), 
                item.getQuantity(),
                new ProductSnapShot(
                    null,
                    item.getProductId(),
                    item.getProductName(),
                    item.getProductCode(),
                    item.getProductPrice(),
                    item.getProductImages(),
                    item.getProductDescription()
                )
            )).toList(),
            command.getName(), 
            command.getPhone(), 
            command.getAddress(), 
            command.getNote()
        );
        
        Order created = repository.create(order);

        eventPublisher.publish(
            new OrderPlacedEvent(created.getUserId()), 
            new EventOptions(OrderQueueConfig.ORDER_PLACED_QUEUE, false)
        );

        return OrderResponse.from(created);
    }
}
