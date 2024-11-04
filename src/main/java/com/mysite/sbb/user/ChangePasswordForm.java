package com.mysite.sbb.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordForm {
    @Size(min = 3, max = 25)
    @NotEmpty(message = "사용자ID는 필수항목입니다.")
    private String username;

    @NotEmpty(message = "기존 비밀번호는 필수항목입니다.")
    private String password;

    @NotEmpty(message = "새 비밀번호는 필수항목입니다.")
    private String new_password1;

    @NotEmpty(message = "새 비밀번호 확인은 필수항목입니다.")
    private String new_password2;
}
