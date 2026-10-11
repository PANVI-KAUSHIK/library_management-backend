package com.library.librarymanagement.dto;

public class UserResponse {
    private Long id;
    private String name;
    private String studentClass;
    private String section;
    private String rollNo;
    private String username;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStudentClass() { return studentClass; }
    public void setStudentClass(String studentClass) { this.studentClass = studentClass; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
