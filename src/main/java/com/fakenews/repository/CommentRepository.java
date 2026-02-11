package com.fakenews.repository;

import com.fakenews.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByHistory_IdOrderByCreatedAtAsc(Long historyId);
    List<Comment> findByHistory_IdAndIsDeletedFalseOrderByCreatedAtAsc(Long historyId);
    Optional<Comment> findByIdAndIsDeletedFalse(Long id);
    List<Comment> findByHistory_IdAndIsDeletedFalseOrderByKarmaDescCreatedAtAsc(Long historyId);


}


