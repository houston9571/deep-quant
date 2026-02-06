package com.optimus.vo;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ApiInfo {

    private String ip;

    private String domain;

    private String theme;


}
