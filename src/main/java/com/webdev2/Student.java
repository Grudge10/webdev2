package com.webdev2;

public class Student implements Gradable {
    private String name;
    private int age;
    private String course;

    public Student(String name, int age, String course) {
        this.name = name;
        setAge(age);
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0) {
            throw new InvalidAgeException("Age cannot be negative: " + age);
        }
        this.age = age;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    @Override
    public String computeStanding() {
        return (age >= 18) ? "Adult" : "Minor";
    }

    @Override
    public String toString() {
        return name + " (Age: " + age + ", Course: " + course + ") - " + computeStanding();
    }
}