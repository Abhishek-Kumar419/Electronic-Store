package com.lcwd.electronic.store.helper;

import com.lcwd.electronic.store.dtos.PageableResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.List;

public class Helper {

    private static final ModelMapper mapper = new ModelMapper();

    public static <U, V> PageableResponse<V> getPageableResponse(Page<U> page, Class<V> type) {

        List<V> dtoList = page.getContent()
                .stream()
                .map(object -> mapper.map(object, type))
                .toList();

        PageableResponse<V> response = new PageableResponse<>();
        response.setContent(dtoList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());

        return response;
    }
}
