package com.mysite.sbb;

import com.mysite.sbb.category.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mysite.sbb.question.QuestionService;

@SpringBootTest
class SbbApplicationTests {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private CategoryService categoryService;


    /*
    @Test
    void testJpa() {
        for (int i = 1; i <= 300; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            this.questionService.create(subject, content, null,null);
        }
    }
    */

    @Test
    void categoryAdd(){
        String name ="question";
        this.categoryService.create(name);

        name ="free";
        this.categoryService.create(name);
    }
}
