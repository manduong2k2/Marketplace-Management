package com.Marketplace_Management.Order.DTOs.Commands;

import java.time.LocalDateTime;

import com.Marketplace_Management.Order.DTOs.Requests.ListOrderRequest;
import com.Marketplace_Management.Shared.DTOs.Commands.PageCommand;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListOrderCommand extends PageCommand {
    private String status;
    private Double totalMin;
    private Double totalMax;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    public static ListOrderCommand fromRequest(ListOrderRequest request) {
        return ListOrderCommand.builder()
                .paging(request)
                .status(blankToNull(request.getStatus()))
                .totalMin(request.getTotalMin())
                .totalMax(request.getTotalMax())
                .dateFrom(request.getDateFrom())
                .dateTo(request.getDateTo())
                .build();
    }
}
