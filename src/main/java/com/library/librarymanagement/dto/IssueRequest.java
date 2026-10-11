package com.library.librarymanagement.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class IssueRequest {
    @NotNull
    private Long memberId;
    @NotNull
    private Long bookId;
    @NotNull
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "[yyyy-MM-dd'T'HH:mm:ss][yyyy-MM-dd HH:mm:ss][yyyy-MM-dd]")
    private LocalDateTime dueDate;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
}