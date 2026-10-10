package com.Marketplace_Management.Shared.DTOs.Commands;

import com.Marketplace_Management.Shared.DTOs.Requests.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** Paging / sorting / search of a list command. Build with XxxCommand.builder().paging(request)... */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class PageCommand extends BaseCommand {
    private int page;
    private int size;
    private String sortBy;
    private String sortOrder;
    /** Trimmed; null when the request had no (or a blank) search. */
    private String search;

    public abstract static class PageCommandBuilder<C extends PageCommand, B extends PageCommandBuilder<C, B>>
            extends BaseCommandBuilder<C, B> {
        /** Copies page, size, sortBy, sortOrder and search from the request. */
        public B paging(PageRequest request) {
            return page(request.getPage())
                    .size(request.getSize())
                    .sortBy(safeTrim(request.getSortBy()))
                    .sortOrder(safeTrim(request.getSortOrder()))
                    .search(blankToNull(request.getSearch()));
        }
    }
}
