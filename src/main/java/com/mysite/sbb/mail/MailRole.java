package com.mysite.sbb.mail;

import lombok.Getter;

@Getter
public enum MailRole {
    PASSWORD("PASSWORD"), DEVICEINFO("DEVICE_INFO");

    private String value;
    MailRole(String value){
        this.value =value;
    }
}
