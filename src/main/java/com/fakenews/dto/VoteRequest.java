package com.fakenews.dto;
import lombok.Data;

@Data
public class VoteRequest {
    private Long historyId;
    private Integer value;

}
