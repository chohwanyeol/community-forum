package com.mysite.sbb.api.todolist;

import com.mysite.sbb.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodolistRepository extends JpaRepository<Todolist, Integer> {
    List<Todolist> findAllByUser(SiteUser user);
}
