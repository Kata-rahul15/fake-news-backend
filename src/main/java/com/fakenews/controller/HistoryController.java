package com.fakenews.controller;

import com.fakenews.model.User;
import com.fakenews.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.fakenews.model.History;

@RestController
@RequestMapping("/api")
public class HistoryController {
    @Autowired private HistoryService historyService;

    @GetMapping("/history")
    public List<History> myHistory(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new RuntimeException("Unauthorized");
        }
        User user = (User) auth.getPrincipal();
        return historyService.getByUser(user);
    }
}
