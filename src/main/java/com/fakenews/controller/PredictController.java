package com.fakenews.controller;

import com.fakenews.dto.PredictRequest;
import com.fakenews.dto.PredictResponse;
import com.fakenews.model.User;
import com.fakenews.repository.UserRepository;
import com.fakenews.service.HistoryService;
import com.fakenews.service.PredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PredictController {

    private final PredictService predictService;
    private final UserRepository userRepository;
    private final HistoryService historyService;

    @PostMapping("/predict")
    public PredictResponse predict(
            @RequestBody PredictRequest req,
            Authentication auth,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String bearer = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            bearer = authHeader.substring(7);
        }

        PredictResponse res;
        System.out.println("RAW REQUEST TYPE = " + req.getType());
        System.out.println("RAW REQUEST TEXT = " + req.getText());
        System.out.println("RAW REQUEST URL = " + req.getUrl());



        switch (req.getType()) {

            case "text":
                res = predictService.predictFromText(req.getText(), bearer, auth);
                break;

            case "url":
                res = predictService.predictFromUrl(req.getUrl(), bearer, auth);
                break;

            case "image":
                return predictService.predictFromImage(
                        req.getImage(),   // base64 string
                        bearer,
                        auth
                );


            default:
                throw new RuntimeException("Invalid request type: " + req.getType());
        }

        return res;
    }
}