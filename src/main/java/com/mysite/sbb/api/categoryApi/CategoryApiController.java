package com.mysite.sbb.api.categoryApi;


import com.mysite.sbb.api.todolist.Todolist;
import com.mysite.sbb.category.Category;
import com.mysite.sbb.category.CategoryService;
import com.mysite.sbb.token.JwtTokenProvider;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("/openapi/category")
@RequiredArgsConstructor
@RestController
public class CategoryApiController {
    private final CategoryService categoryService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<?> readALL(){
        //String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //SiteUser siteUser = userService.getUser(username);
        List<CategoryApiDTO> categoryApiDTOS = categoryService.getIdName();
        return ResponseEntity.ok(categoryApiDTOS);
    }


}
