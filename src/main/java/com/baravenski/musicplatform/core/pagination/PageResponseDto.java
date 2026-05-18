package com.baravenski.musicplatform.core.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NullMarked
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private boolean last;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;

    public PageResponseDto(Page<T> page) {
        this.content = page.getContent();
        this.last = page.isLast();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.size = page.getSize();
        this.number = page.getNumber();
    }
}
