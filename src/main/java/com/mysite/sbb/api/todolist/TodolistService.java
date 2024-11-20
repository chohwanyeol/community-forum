package com.mysite.sbb.api.todolist;

import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class TodolistService {
    private final TodolistRepository todolistRepository;

    public List<Todolist> getALL(SiteUser siteUser) {
        return todolistRepository.findAllByUser(siteUser);
    }

    public Todolist create(TodolistDTO dto,SiteUser siteUser) {
        Todolist todolist = dto.toEntity();
        todolist.setUser(siteUser);
        return todolistRepository.save(todolist);
    }


    public Todolist update(TodolistDTO dto,Integer id, SiteUser siteUser) {
        Todolist target = todolistRepository.findById(id).orElse(null);
        if (target== null){
            return null;
        }
        Todolist todolist = dto.toEntity();
        target.patch(todolist);
        Todolist updated = todolistRepository.save(target);
        return updated;
    }

    public Todolist delete(Integer id, SiteUser siteUser) {
        Todolist target = todolistRepository.findById(id).orElse(null);
        if (target == null){
            return null;
        }
        todolistRepository.delete(target);
        return target;
    }
}
