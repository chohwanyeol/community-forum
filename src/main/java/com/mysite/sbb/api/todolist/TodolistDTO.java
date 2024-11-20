package com.mysite.sbb.api.todolist;


import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TodolistDTO {
    private String content;
    private LocalDateTime createDate = LocalDateTime.now();

    public Todolist toEntity(){
        return new Todolist( this.content, this.createDate);
    }


}
