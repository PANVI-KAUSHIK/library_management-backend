package com.library.librarymanagement.repository;

import com.library.librarymanagement.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByStudentClassAndSectionAndRollNo(String studentClass, String section, String rollNo);
}
