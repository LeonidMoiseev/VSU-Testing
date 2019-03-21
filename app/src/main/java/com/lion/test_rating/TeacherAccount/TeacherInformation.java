package com.lion.test_rating.TeacherAccount;

public class TeacherInformation {

    private String name;
    private String email;
    private String department;

    TeacherInformation() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String getDepartment() {
        return department;
    }

    void setDepartment(String department) {
        this.department = department;
    }
}
