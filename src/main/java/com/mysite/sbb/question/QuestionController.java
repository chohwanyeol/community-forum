package com.mysite.sbb.question;

import java.security.Principal;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.category.Category;
import com.mysite.sbb.category.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/board")
@RequiredArgsConstructor
@Controller
public class QuestionController {

	private final QuestionService questionService;
	private final AnswerService answerService;
	private final UserService userService;
	private final CategoryService categoryService;

	@GetMapping("/{categoryName}/list")
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "kw", defaultValue = "") String kw, @PathVariable("categoryName") String categoryName) {
		log.info("page:{}, kw:{}", page, kw);
		Category category = this.categoryService.getCategory(categoryName);
		Page<Question> paging = this.questionService.getList(page, kw,category);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		model.addAttribute("categoryName",categoryName);

		return "question_list";
	}



	/*
	@GetMapping(value = "/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
		Question question = this.questionService.getQuestion(id);
		model.addAttribute("question", question);
		return "question_detail";
	}
	*/

	//20240726 answer paging 처리
	@GetMapping(value = "/{categoryName}/detail/{id}")
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
					   @RequestParam(value = "order", defaultValue = "recent") String order,
					   @PathVariable("id") Integer id, AnswerForm answerForm,
					   @PathVariable("categoryName") String categoryName){
		Question question = this.questionService.getQuestion(id);
		Page<Answer> paging = this.answerService.getList(question, page, order);
		model.addAttribute("question", question);
		model.addAttribute("paging", paging);
		model.addAttribute("order", order);
		return "question_detail";
	}



	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{categoryName}/create")
	public String questionCreate(QuestionForm questionForm) {
		return "question_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{categoryName}/create")
	public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal,
								 @PathVariable("categoryName") String categoryName) {
		if (bindingResult.hasErrors()) {
			return "question_form";
		}
		SiteUser siteUser = this.userService.getUser(principal.getName());
		System.out.println(categoryName);
		Category category = this.categoryService.getCategory(categoryName);
		this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser,category);
		return String.format("redirect:/board/%s/list",categoryName);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{categoryName}/modify/{id}")
	public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
		Question question = this.questionService.getQuestion(id);
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		questionForm.setSubject(question.getSubject());
		questionForm.setContent(question.getContent());
		return "question_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{categoryName}/modify/{id}")
	public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal,
								 @PathVariable("id") Integer id,
								 @PathVariable("categoryName") String categoryName)
	{
		if (bindingResult.hasErrors()) {
			return "question_form";
		}
		Question question = this.questionService.getQuestion(id);
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
		return String.format("redirect:/board/%s/detail/%s", categoryName,id);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{categoryName}/delete/{id}")
	public String questionDelete(Principal principal, @PathVariable("id") Integer id,
								 @PathVariable("categoryName") String categoryName) {
		Question question = this.questionService.getQuestion(id);
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}
		this.questionService.delete(question);
		return String.format("redirect:/board/%s/list",categoryName);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{categoryName}/vote/{id}")
	public String questionVote(Principal principal, @PathVariable("id") Integer id,
							   @PathVariable("categoryName") String categoryName) {
		Question question = this.questionService.getQuestion(id);
		SiteUser siteUser = this.userService.getUser(principal.getName());
		this.questionService.vote(question, siteUser);
		return String.format("redirect:/board/%s/detail/%s",categoryName, id);
	}
}
