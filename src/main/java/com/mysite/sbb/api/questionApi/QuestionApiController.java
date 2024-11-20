package com.mysite.sbb.api.questionApi;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.device.DeviceService;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.token.JwtTokenProvider;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/openapi/question")
@RestController
@RequiredArgsConstructor
public class QuestionApiController {
    private final QuestionService questionService;


    @GetMapping("")
    public ResponseEntity<?> readALL(){

        List<QuestionApiDTO> questionApiDTOs = questionService.getAllDtoList();
        return ResponseEntity.ok(questionApiDTOs);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> readUsername(@PathVariable String username){

        List<QuestionApiDTO> questionApiDTOs = questionService.getUsernameDtoList(username);
        return ResponseEntity.ok(questionApiDTOs);
    }

    @PostMapping("/{username}")
    public ResponseEntity<?> createUsername(@PathVariable String username, @Validated @RequestBody QuestionApiDTO questionApiDTO){
        SiteUser siteUser = (SiteUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (username.equals(siteUser.getUsername())){
            try {
                questionService.create(questionApiDTO,siteUser);
            }catch (DataNotFoundException e){
                e.printStackTrace();
                return ResponseEntity.badRequest().body("invalid data");
            }

            return ResponseEntity.ok("Question created successfully!");
        }
        else{
            return ResponseEntity.badRequest().body("invalid data");
        }
    }



    @PatchMapping("/{username}/{id}")
    public ResponseEntity<?> updateUsernameId(@PathVariable String username, @PathVariable Integer id,
                                            @Validated @RequestBody QuestionApiDTO questionApiDTO){
        SiteUser siteUser = (SiteUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (username.equals(siteUser.getUsername())){
            try {
                questionService.update(questionApiDTO, id);
            }catch (DataNotFoundException e){
                e.printStackTrace();
                return ResponseEntity.badRequest().body("invalid data");
            }

            return ResponseEntity.ok("Question Updated successfully!");
        }
        else{
            return ResponseEntity.badRequest().body("invalid data");
        }
    }

    @DeleteMapping("/{username}/{id}")
    public ResponseEntity<?> deleteUsernameId(@PathVariable String username, @PathVariable Integer id){
        SiteUser siteUser = (SiteUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (username.equals(siteUser.getUsername())){
            try {
                questionService.delete(id);
            }catch (DataNotFoundException e){
                e.printStackTrace();
                return ResponseEntity.badRequest().body("invalid data");
            }

            return ResponseEntity.ok("Question Deleted successfully!");
        }
        else{
            return ResponseEntity.badRequest().body("invalid data");
        }
    }







}
