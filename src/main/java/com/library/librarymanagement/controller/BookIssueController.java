package com.library.librarymanagement.controller;

import com.library.librarymanagement.dto.IssueRequest;
import com.library.librarymanagement.entity.BookIssue;
import com.library.librarymanagement.service.BookIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class BookIssueController {

    @Autowired
    private BookIssueService bookIssueService;

    // Issue a book to a member
    @PostMapping
    public ResponseEntity<BookIssue> issueBook(@RequestBody IssueRequest request) {
        BookIssue created = bookIssueService.issueBook(request);
        return ResponseEntity.ok(created);
    }

    // Return a book by issue record ID
    @PutMapping("/{issueId}/return")
    public ResponseEntity<BookIssue> returnBook(@PathVariable Long issueId) {
        BookIssue returned = bookIssueService.returnBook(issueId);
        return ResponseEntity.ok(returned);
    }

    // Get all issues for a specific member
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BookIssue>> getIssuesByMember(@PathVariable Long memberId) {
        List<BookIssue> issues = bookIssueService.getIssuesByMember(memberId);
        return ResponseEntity.ok(issues);
    }

    // Get all issues
    @GetMapping
    public ResponseEntity<List<BookIssue>> getAllIssues() {
        return ResponseEntity.ok(bookIssueService.getAllIssues());
    }

    // Count total issues
    @GetMapping("/count")
    public long countIssues() {
        return bookIssueService.countIssues();
    }
}
