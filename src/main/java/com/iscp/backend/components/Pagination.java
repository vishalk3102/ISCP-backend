package com.iscp.backend.components;

import com.iscp.backend.dto.PaginatedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Pagination {


    private static final Logger log = LoggerFactory.getLogger(Pagination.class);

    //FUNCTION TO CREATE PAGEABLE
    public static Pageable createPageable(int page,int size,String sortBy,Boolean isAscending)
    {
        Sort sort= isAscending? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        return PageRequest.of(page,size,sort);
    }

    public static Pageable createPageableWithMultipleSort(int page, int size) {
        Sort sort = Sort.by(Sort.Order.desc("creationTime"));
        return PageRequest.of(page, size, sort);
    }

    public static Pageable createPageableNoSort(int page,int size)
    {
        return PageRequest.of(page,size);
    }


    //FUNCTION TO CREATE PAGINATED RESPONSE
    public static <T> PaginatedResponse<T> createdPaginatedContent(Page<T> pageData)
    {
        PaginatedResponse paginatedResponse=new PaginatedResponse<>();
        paginatedResponse.setPageNumber(pageData.getNumber());
        paginatedResponse.setPageSize(pageData.getSize());
        paginatedResponse.setTotalPages(pageData.getTotalPages());
        paginatedResponse.setTotalElements(pageData.getTotalElements());
        paginatedResponse.setContent(pageData.getContent());
        return paginatedResponse;
    }
}
