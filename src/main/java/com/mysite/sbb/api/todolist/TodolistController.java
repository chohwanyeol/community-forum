package com.mysite.sbb.api.todolist;



import com.mysite.sbb.token.JwtTokenProvider;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/openapi/todolist")
@RestController
@RequiredArgsConstructor
public class TodolistController {
    @Autowired
    private final UserService userService;
    private final TodolistService todolistService;
    private final JwtTokenProvider jwtTokenProvider;


    @GetMapping("")
    public ResponseEntity<?> readAllTodolist(@RequestHeader("Authorization") String authorization){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SiteUser siteUser = userService.getUser(username);
        List<Todolist> todolists = todolistService.getALL(siteUser);
        return ResponseEntity.ok(todolists);
    }


    @PostMapping("")
    public ResponseEntity<?> createTodolist(@RequestHeader("Authorization") String authorization,
                                            @RequestBody TodolistDTO dto){
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("인증토큰이 없거나 유효하지 않음");
        }
        //    토큰번호로 사용자 찾아서 이름반환(String userName)
        String username = jwtTokenProvider.getUsernameFromToken(authorization).substring(7);
        SiteUser siteUser = userService.getUser(username);
        Todolist todolist = todolistService.create(dto,siteUser);
        return todolist != null? ResponseEntity.status(HttpStatus.OK).body(todolist) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Object)null);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTodolist(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authorization,
            @RequestBody TodolistDTO dto)
    {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("인증토큰이 없거나 유효하지 않음");
        }
        //    토큰번호로 사용자 찾아서 이름반환(String userName)
        String username = jwtTokenProvider.getUsernameFromToken(authorization).substring(7);
        SiteUser siteUser = userService.getUser(username);
        Todolist todolist = todolistService.update(dto,id,siteUser);
        return todolist != null? ResponseEntity.status(HttpStatus.OK).body(todolist) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Object)null);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodolist(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authorization
           )
    {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("인증토큰이 없거나 유효하지 않음");
        }
        //    토큰번호로 사용자 찾아서 이름반환(String userName)
        String username = jwtTokenProvider.getUsernameFromToken(authorization).substring(7);
        SiteUser siteUser = userService.getUser(username);
        Todolist todolist = todolistService.delete(id,siteUser);
        return todolist != null? ResponseEntity.status(HttpStatus.OK).body(todolist) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Object)null);

    }

    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }


}
