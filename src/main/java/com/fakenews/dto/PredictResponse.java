package com.fakenews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictResponse {

    private String finalLabel;
    private double confidence;
    private String summary;
    private String explanation;
    private List<String> keywords;
    private String language;
    private Map<String, Object> sentiment;
    private boolean factCheckUsed;
    private String factCheckSource;
    private String verificationMethod;
    private List<Evidence> evidence;
}
