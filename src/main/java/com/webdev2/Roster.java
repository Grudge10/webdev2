package com.webdev2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Roster {
    private List<Student> students;

    public Roster() {
        this.students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public List<Student> getStudents() {
        return students;
    }

    public void generateStreamReport() {
        List<String> adultNames = students.stream()
                .filter(s -> s.getAge() >= 18)
                .map(Student::getName)
                .collect(Collectors.toList());

        System.out.println("--- Stream-Based Roster Report ---");
        System.out.println("Qualified Students (18+): " + adultNames);
        System.out.println("Total Qualified Count: " + adultNames.size());
    }
}