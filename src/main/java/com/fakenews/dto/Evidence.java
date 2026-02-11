package com.fakenews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evidence {
    private String source;
    private String url;
    private String title;
    private String snippet;
}
