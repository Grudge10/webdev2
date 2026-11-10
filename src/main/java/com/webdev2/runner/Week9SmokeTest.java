package com.webdev2.runner;

import com.webdev2.model.*;
import com.webdev2.repository.*;
import com.webdev2.service.EmployeeService;
import org.hibernate.LazyInitializationException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class Week9SmokeTest implements CommandLineRunner {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final OwnerRepository ownerRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmployeeService employeeService;

    public Week9SmokeTest(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            OwnerRepository ownerRepository,
            VeterinarianRepository veterinarianRepository,
            AppointmentRepository appointmentRepository,
            EmployeeService employeeService
    ) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.ownerRepository = ownerRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.appointmentRepository = appointmentRepository;
        this.employeeService = employeeService;
    }

    @Override
    public void run(String... args) {
        if (departmentRepository.count() > 0) {
            return;
        }

        User u = new User("mreyes", "mreyes@example.com");
        u.setProfile(new UserProfile("Vet clinic staff", "09171234567"));
        userRepository.save(u);

        Department dept = new Department("Engineering");
        Employee emp1 = new Employee("Alice", "Developer");
        Employee emp2 = new Employee("Bob", "Tester");
        dept.addEmployee(emp1);
        dept.addEmployee(emp2);
        departmentRepository.save(dept);
        System.out.println("Employees after Department save: " + employeeRepository.count());

        Student student = new Student("Carlos");
        Course course = new Course("CS101", "Intro to Programming");
        studentRepository.save(student);
        courseRepository.save(course);

        Enrollment enrollment1 = new Enrollment(student, course, LocalDate.now(), "A");
        enrollmentRepository.save(enrollment1);
        System.out.println("Saved enrollment id: " + enrollment1.getId());

        try {
            Enrollment duplicate = new Enrollment(student, course, LocalDate.now(), "B");
            enrollmentRepository.save(duplicate);
        } catch (DataIntegrityViolationException ex) {
            System.out.println("Duplicate enrollment rejected: " + ex.getMostSpecificCause().getMessage());
        }

        try {
            employeeService.getDepartmentNameWithoutTransaction(emp1.getId());
        } catch (LazyInitializationException ex) {
            System.out.println("LazyInitializationException reproduced: " + ex.getMessage());
        }

        System.out.println("Transactional fix dept: " + employeeService.getDepartmentNameTransactional(emp1.getId()));
        System.out.println("Fetch join fix dept: " + employeeService.getDepartmentNameFetchJoin(emp1.getId()));

        dept.removeEmployee(emp2);
        departmentRepository.save(dept);
        System.out.println("Employees after orphanRemoval: " + employeeRepository.count());

        Student tempStudent = new Student("TempStudent");
        Course sharedCourse = new Course("WEB202", "Spring Boot");
        tempStudent.addCourse(sharedCourse);
        studentRepository.save(tempStudent);
        studentRepository.delete(tempStudent);
        System.out.println("Shared course still exists after student delete: " + courseRepository.existsById(sharedCourse.getId()));

        Owner owner = new Owner("Maria Santos", "09181112222");
        Pet clinicPet = new Pet("MC-9001", "Bantay", "Dog", "Aspin");
        owner.addPet(clinicPet);
        ownerRepository.save(owner);

        Veterinarian vet = new Veterinarian("Dr. Reyes", "Surgery");
        veterinarianRepository.save(vet);

        Appointment appt = new Appointment(clinicPet, vet, LocalDate.now(), "Annual Checkup");
        appointmentRepository.save(appt);
        System.out.println("Saved appointment id: " + appt.getId() + " for pet " + clinicPet.getName());
    }
}
