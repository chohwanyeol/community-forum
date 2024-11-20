package com.mysite.sbb.api.categoryApi;


import com.mysite.sbb.category.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CategoryApiDTO {
    private Integer id;
    private String name;
}
