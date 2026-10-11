package com.library.librarymanagement.repository;

import com.library.librarymanagement.entity.BookIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    List<BookIssue> findByMemberId(Long memberId);
    List<BookIssue> findByBookId(Long bookId);
    Optional<BookIssue> findByMemberIdAndBookIdAndStatus(Long memberId, Long bookId, String status);
}
