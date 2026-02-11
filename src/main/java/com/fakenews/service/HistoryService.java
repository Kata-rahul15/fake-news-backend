package com.fakenews.service;

import com.fakenews.model.History;
import com.fakenews.model.User;
import com.fakenews.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {
    @Autowired private HistoryRepository historyRepository;

    public History save(User user, String text, String label, Double confidence, String explanation) {
        History h = new History();
        h.setUser(user);
        h.setText(text);
        h.setLabel(label);
        h.setConfidence(confidence);
        h.setExplanation(explanation);
        return historyRepository.save(h);
    }


    public List<History> getByUser(User user) {
        return historyRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
