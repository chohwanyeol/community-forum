package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.mail.EmailException;
import com.mysite.sbb.question.QuestionForm;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm) {
		return "signup_form";
	}

	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "signup_form";
		}

		if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			return "signup_form";
		}

		try {
			userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "signup_form";
		} catch (Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "signup_form";
		}

		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {
		return "login_form";
	}


	@GetMapping("/findPassword")
	public String findPassword(FindPasswordForm findPasswordForm){
		return "find_password";
	}


	@PostMapping("/findPassword")
	public String findPassword(@Valid FindPasswordForm findPasswordForm,
							   BindingResult bindingResult,
							   Model model){
		if (bindingResult.hasErrors()) {
			return "find_password";
		}
		try {
			userService.mailTempPassword(findPasswordForm.getUsername(),findPasswordForm.getEmail());
		} catch (DataNotFoundException e) {
			e.printStackTrace();
			bindingResult.reject("emailNotFound", e.getMessage());
			return "find_password";
		} catch (EmailException e) {
			e.printStackTrace();
			bindingResult.reject("sendEmailFail", e.getMessage());
			return "find_password";
		}
		return "redirect:/";
	}

	@GetMapping("/changePassword")
	public String signup(ChangePasswordForm changePasswordForm) {
		return "change_password";
	}

	@PostMapping("/changePassword")
	public String changePassword(@Valid ChangePasswordForm changePasswordForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "change_password";
		}

		if (!changePasswordForm.getNew_password1().equals(changePasswordForm.getNew_password2())) {
			bindingResult.rejectValue("password", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			return "change_password";
		}
		if(!this.userService.changePassword(changePasswordForm.getUsername(),changePasswordForm.getPassword(),changePasswordForm.getNew_password1())){
			bindingResult.rejectValue("password", "passwordInCorrect", "기존 패스워드가 일치하지 않습니다.");
			return "change_password";
		}

		return "redirect:/";
	}

}
