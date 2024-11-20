package com.mysite.sbb.api.todolist;


import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
public class Todolist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(4000)")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private SiteUser user;

    public Todolist(String content, LocalDateTime createDate) {
        this.content = content;
        this.createDate = createDate;
    }
    public Todolist(Integer id, String content, LocalDateTime createDate) {
        this.id = id;
        this.content = content;
        this.createDate = createDate;
    }

    public void patch(Todolist todolist) {
        this.content = todolist.getContent();
        this.createDate = todolist.getCreateDate();
    }
}
