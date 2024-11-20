package com.mysite.sbb.api.questionApi;


import com.mysite.sbb.category.Category;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class QuestionApiDTO {
    private Integer id;

    @NotNull
    private String subject;

    @NotNull
    private String content;

    private LocalDateTime createDate;

    private String author_Username;

    private LocalDateTime modifyDate;

    private String categoryName;

    public Question toEntity() {
        Question question = new Question();
        question.setSubject(this.getSubject());
        question.setContent(this.getContent());
        question.setCreateDate(LocalDateTime.now());
        return question;
    }
}
