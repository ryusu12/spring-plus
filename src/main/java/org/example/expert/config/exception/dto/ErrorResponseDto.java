package org.example.expert.config.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {

    private String status;
    private Integer code;
    private String message;

}