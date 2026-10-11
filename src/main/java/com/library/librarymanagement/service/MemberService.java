package com.library.librarymanagement.service;

import com.library.librarymanagement.dto.MemberRequest;
import com.library.librarymanagement.entity.Member;
import com.library.librarymanagement.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Member createMember(MemberRequest request) {
        if (memberRepository.findByStudentClassAndSectionAndRollNo(request.getStudentClass(), request.getSection(), request.getRollNo()).isPresent()) {
            throw new IllegalArgumentException("Member with same class, section and roll number already exists");
        }
        Member member = new Member();
        member.setName(request.getName());
        member.setStudentClass(request.getStudentClass());
        member.setSection(request.getSection());
        member.setRollNo(request.getRollNo());
        member.setContactInfo(request.getContactInfo());
        return memberRepository.save(member);
    }

    public Member updateMember(Long id, MemberRequest request) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Member not found"));
        member.setName(request.getName());
        member.setStudentClass(request.getStudentClass());
        member.setSection(request.getSection());
        member.setRollNo(request.getRollNo());
        member.setContactInfo(request.getContactInfo());
        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Member not found"));
        memberRepository.delete(member);
    }
    public long countMembers() {
        return memberRepository.count();
    }

}
