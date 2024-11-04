package com.mysite.sbb.category;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category getCategory(String categoryName){
        Category category = this.categoryRepository.findByName(categoryName);
        if (category!=null) {
            return category;
        } else {
            throw new DataNotFoundException("category not found");
        }
    }

    public void create(String name) {
        Category c = new Category();
        c.setName(name);
        this.categoryRepository.save(c);
    }

}
