package com.webdev2;

public class Main {
    public static void main(String[] args) {
        Roster roster = new Roster();

        roster.addStudent(new Student("Alice", 20, "Computer Science"));
        roster.addStudent(new Student("Bob", 17, "Mathematics"));
        roster.addStudent(new Student("Charlie", 19, "Physics"));
        roster.addStudent(new Student("Diana", 16, "Biology"));
        roster.addStudent(new Student("Eve", 22, "Engineering"));
        roster.addStudent(new Student("Frank", 18, "History"));

        System.out.println("--- Full Student Roster ---");
        roster.getStudents().forEach(System.out::println);

        System.out.println("\n--- Testing Invalid Age Exception ---");
        try {
            roster.addStudent(new Student("Ghost", -5, "Unknown"));
        } catch (InvalidAgeException e) {
            System.out.println("Caught exception: " + e.getMessage());
            System.out.println("continuing execution...");
        }

        System.out.println();
        roster.generateStreamReport();
    }
}