package com.Marketplace_Management.Order.DTO.Commands;

import java.time.LocalDateTime;

import com.Marketplace_Management.Order.DTO.Requests.ListOrderRequest;

import lombok.Data;

@Data
public class ListOrderCommand {
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortOrder = "desc";

    private String search;
    private String status;
    private Double totalMin;
    private Double totalMax;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    public static ListOrderCommand fromRequest(ListOrderRequest request) {
        ListOrderCommand command = new ListOrderCommand();
        command.setPage(request.getPage());
        command.setSize(request.getSize());
        command.setSortBy(request.getSortBy());
        command.setSortOrder(request.getSortOrder());
        command.setSearch(request.getSearch());
        command.setStatus(request.getStatus());
        command.setTotalMin(request.getTotalMin());
        command.setTotalMax(request.getTotalMax());
        command.setDateFrom(request.getDateFrom());
        command.setDateTo(request.getDateTo());
        return command;
    }
}
