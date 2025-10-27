package com.example.demo.dto;

import lombok.Data;

@Data
public class RequestSendMailDTO {
    private String to;
    private String subject;
    private String name;
}
