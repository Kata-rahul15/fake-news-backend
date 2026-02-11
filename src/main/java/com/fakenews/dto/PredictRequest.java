package com.fakenews.dto;

import lombok.Data;

@Data
public class PredictRequest {

    private String type;   // "text" | "url" | "image"
    private String text;   // used when type = text
    private String url;  // used when type = url
    private String image;
}
