package com.mysite.sbb.category;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@RequestMapping("/category")
@RequiredArgsConstructor
@Controller
public class CategoryController {
    private final CategoryService categoryService;
    
}
