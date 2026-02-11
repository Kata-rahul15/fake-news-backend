package com.fakenews.service;

import com.fakenews.dto.AdminOverviewResponse;
import com.fakenews.dto.AdminReportResponse;
import com.fakenews.model.History;
import com.fakenews.repository.HistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminOverviewService {

    private final HistoryRepository historyRepository;

    public AdminOverviewService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public AdminOverviewResponse getOverview() {

        long total = historyRepository.count();

        // IMPORTANT:
        // These labels MUST stay lowercase: real / fake / unverifiable
        // If PredictResponse.finalLabel changes, update here too
        long real = historyRepository.countByLabel("real");
        long fake = historyRepository.countByLabel("fake");
        long unverifiable = historyRepository.countByLabel("unverifiable");

        /* =========================
           WEEKLY DATA (LAST 7 DAYS)
           ========================= */

        LocalDate today = LocalDate.now();
        LocalDateTime startDate = today.minusDays(6).atStartOfDay();

        // Fetch last 7 days history
        List<History> lastWeek =
                historyRepository.findByCreatedAtAfter(startDate);

        // Prepare all 7 days (even if count = 0)
        Map<LocalDate, Map<String, Object>> dayMap = new LinkedHashMap<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);

            Map<String, Object> stats = new HashMap<>();
            stats.put("day", day.getDayOfWeek().toString().substring(0, 3));
            stats.put("real", 0);
            stats.put("fake", 0);
            stats.put("unverifiable", 0);

            dayMap.put(day, stats);
        }

        // Fill counts from DB
        for (History h : lastWeek) {
            if (h.getCreatedAt() == null || h.getLabel() == null) continue;

            LocalDate date = h.getCreatedAt().toLocalDate();
            Map<String, Object> stats = dayMap.get(date);
            if (stats == null) continue;

            String label = h.getLabel();

            // IMPORTANT:
            // Only these three labels are supported
            stats.put(label, (int) stats.get(label) + 1);
        }

        List<Map<String, Object>> weeklyData =
                new ArrayList<>(dayMap.values());

        return new AdminOverviewResponse(
                total,
                fake,
                real,
                unverifiable,
                weeklyData
        );
    }
    public List<AdminReportResponse> getReports() {

        List<History> historyList =
                historyRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<AdminReportResponse> reports = new ArrayList<>();

        for (History h : historyList) {

            String status =
                    h.getLabel().substring(0, 1).toUpperCase()
                            + h.getLabel().substring(1);

            reports.add(
                    new AdminReportResponse(
                            h.getId(),
                            h.getText(),
                            status
                    )
            );
        }

        return reports;
    }

    public List<Map<String, Object>> getDomains() {

        List<Object[]> rows = historyRepository.getDomainLabelCounts();

        Map<String, Map<String, Integer>> domainMap = new HashMap<>();

        // Group counts per domain
        for (Object[] row : rows) {
            String domain = (String) row[0];
            String label = (String) row[1];
            int count = ((Long) row[2]).intValue();

            domainMap.putIfAbsent(domain, new HashMap<>());
            domainMap.get(domain).put(label, count);
        }

        List<Map<String, Object>> response = new ArrayList<>();

        for (String domain : domainMap.keySet()) {

            Map<String, Integer> counts = domainMap.get(domain);

            int real = counts.getOrDefault("real", 0);
            int fake = counts.getOrDefault("fake", 0);
            int unverifiable = counts.getOrDefault("unverifiable", 0);

            Map<String, Object> domainData = new HashMap<>();
            domainData.put("domain", domain);
            domainData.put("real", real);
            domainData.put("fake", fake);
            domainData.put("unverifiable", unverifiable);

            response.add(domainData);
        }

        return response;
    }

}
