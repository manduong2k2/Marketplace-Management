package com.Marketplace_Management.Vendor.DTOs.Command;

import com.Marketplace_Management.Vendor.DTOs.Request.CreateCollectionRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectionCommand {
    private String name;
    private Integer displayOrder;

    public static CreateCollectionCommand fromRequest(CreateCollectionRequest request) {
        CreateCollectionCommand command = new CreateCollectionCommand();
        command.setName(request.getName());
        command.setDisplayOrder(request.getDisplayOrder());
        return command;
    }
}
