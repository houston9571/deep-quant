package com.optimus.sprider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SpriderTemplate {

    private String siteName;
    private String description;
    private String responseFormat;
    private String requestMethod;
    private String requestBody;
    private String encoding = "UTF-8";
    private boolean executeJavaScript;
    private String url;
    List<AnalysisFactor> factors = Lists.newLinkedList();
}


