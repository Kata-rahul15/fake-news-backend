package com.fakenews.repository;

import com.fakenews.model.History;
import com.fakenews.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUserOrderByCreatedAtDesc(User user);

        long count();

        long countByLabel(String label);

    List<History> findByCreatedAtAfter(LocalDateTime date);


    @Query("""
        SELECT h.domain, h.label, COUNT(h)
        FROM History h
        WHERE h.domain IS NOT NULL
        GROUP BY h.domain, h.label
    """)
    List<Object[]> getDomainLabelCounts();
    }


