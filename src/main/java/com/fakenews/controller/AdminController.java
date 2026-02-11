package com.fakenews.controller;

import com.fakenews.dto.AdminOverviewResponse;
import com.fakenews.dto.AdminReportResponse;
import com.fakenews.model.Comment;
import com.fakenews.model.User;
import com.fakenews.model.UserStatus;
import com.fakenews.repository.CommentRepository;
import com.fakenews.repository.HistoryRepository;
import com.fakenews.repository.UserRepository;
import com.fakenews.service.AdminOverviewService;
import com.fakenews.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.*;


@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final UserRepository userRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private BadgeService badgeService;


    // ✅ ADD THIS (for overview)
    private final AdminOverviewService overviewService;

    private final CommentRepository commentRepo;






    // ✅ UPDATE CONSTRUCTOR (do NOT remove UserRepository)
    public AdminController(
            UserRepository userRepository,
            AdminOverviewService overviewService,
            CommentRepository commentRepo
    ) {
        this.userRepository = userRepository;
        this.overviewService = overviewService;
        this.commentRepo = commentRepo;
    }

    /* =========================
       USERS – ADMIN MANAGEMENT
       ========================= */

    // 🔹 Get all users for Admin Dashboard
    @GetMapping("/users")
    public List<Map<String, Object>> getAllUsers() {


        List<Map<String, Object>> response = new ArrayList<>();

        for (User u : userRepository.findAll()) {

            Map<String, Object> user = new HashMap<>();
            user.put("id", u.getId());
            user.put("name", u.getFullName());
            user.put("email", u.getEmail());

            // 🔥 Mapping for frontend
            user.put("impact", u.getKarmaPoints());          // Impact Score
            user.put("activity", u.getChecksPerformed());    // Activity
            user.put(
                    "status",
                    u.getStatus() != null ? u.getStatus().name() : "ACTIVE"
            );
            // ACTIVE / WARNED / SUSPENDED
            user.put("warnings", u.getWarnings());

            response.add(user);
        }

        return response;
    }

    // ⚠️ Warn user
    @PostMapping("/users/{id}/warn")
    public void warnUser(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setWarnings(user.getWarnings() + 1);
        user.setKarmaPoints(Math.max(0, user.getKarmaPoints() - 5));
        user.setBadge(badgeService.calculateBadge(user.getKarmaPoints()));
        user.setStatus(UserStatus.WARNED);
        user.setWarningAcknowledged(false);

        userRepository.save(user);
    }


    // ⛔ Suspend user
    @PostMapping("/users/{id}/suspend")
    public void suspendUser(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.SUSPENDED);

        userRepository.save(user);

    }

    /* =========================
       OVERVIEW – ADMIN ANALYTICS
       ========================= */

    @GetMapping("/overview")
    public AdminOverviewResponse getAdminOverview() {
        return overviewService.getOverview();
    }
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {

        Comment comment = commentRepo
                .findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setDeleted(true);
        commentRepo.save(comment);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/reports")
    public List<AdminReportResponse> getAdminReports() {
        return overviewService.getReports();
    }


    @DeleteMapping("/reports/{id}")
    public void deleteReport(@PathVariable Long id) {

        if (!historyRepository.existsById(id)) {
            throw new RuntimeException("Report not found");
        }

        historyRepository.deleteById(id);
    }
    @GetMapping("/domains")
    public List<Map<String, Object>> getDomains() {
        return overviewService.getDomains();

    }



}
