package com.Project1.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PageResponse<T> {

    int pageNo;
    int pageSize;
    int totalPage;
    T items;
}