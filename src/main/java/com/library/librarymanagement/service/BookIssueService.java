package com.library.librarymanagement.service;

import com.library.librarymanagement.dto.IssueRequest;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BookIssue;
import com.library.librarymanagement.entity.Member;
import com.library.librarymanagement.repository.BookIssueRepository;
import com.library.librarymanagement.repository.BookRepository;
import com.library.librarymanagement.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookIssueService {

    @Autowired
    private BookIssueRepository issueRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    // Issue a book to a member
    public BookIssue issueBook(IssueRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalArgumentException("No available copies for this book");
        }
        // Create issue record
        BookIssue issue = new BookIssue();
        issue.setMember(member);
        issue.setBook(book);
        issue.setDueDate(request.getDueDate());
        issue.setStatus("ISSUED");
        issue.setIssueDate(LocalDateTime.now());
        // Update book inventory
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        return issueRepository.save(issue);
    }

    // Return a previously issued book
    public BookIssue returnBook(Long issueId) {
        BookIssue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue record not found"));
        if (!"ISSUED".equals(issue.getStatus())) {
            throw new IllegalArgumentException("Book is not currently issued");
        }
        issue.setReturnDate(LocalDateTime.now());
        issue.setStatus("RETURNED");
        // Increment available copies
        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        return issueRepository.save(issue);
    }

    // Fetch issues by member
    public List<BookIssue> getIssuesByMember(Long memberId) {
        return issueRepository.findByMemberId(memberId);
    }

    // Fetch issues by book
    public List<BookIssue> getIssuesByBook(Long bookId) {
        return issueRepository.findByBookId(bookId);
    }
    // Fetch all issues
    public List<BookIssue> getAllIssues() {
        return issueRepository.findAll();
    }

    // Count total issue records
    public long countIssues() {
        return issueRepository.count();
    }

}
