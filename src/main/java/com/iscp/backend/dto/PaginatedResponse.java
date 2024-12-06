package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pagination Data Response")
public class PaginatedResponse<T> {

    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<T> content;
}
