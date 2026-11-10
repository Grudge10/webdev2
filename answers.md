# WEBDEV2 — Week 9 Lab Answers

Name: Jan Liam Arias
Student ID:
GitHub repo: https://github.com/Grudge10/webdev2
Date submitted: 2026-11-10

---

## Task 1 — One-to-One + One-to-Many Mappings

**Approach / explanation:**
The User class was used as the owning side by adding @OneToOne annotation along with @JoinColumn(name="profile_id") and the UserProfile class was used as the inverse side by adding @OneToOne(mappedBy = "profile"). The Employee class was used as the owning side by adding @ManyToOne(fetch = FetchType.LAZY) along with @JoinColumn(name = "department_id", nullable = false) and the Department class was used as the inverse side by adding @OneToMany(mappedBy = "department", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true). Also, addEmployee() and removeEmployee() were added as helper methods in the Department class so that when an employee is added or removed from a department, the employees list in the Department class and the department field in the Employee class are updated.

**Code / endpoint(s) used (if applicable):**
```java
@Entity
@Table(name = "app_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;

    public void setProfile(UserProfile profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setUser(this);
        }
    }
}

@Entity
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;
    private String phone;

    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY)
    private User user;
}

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "department", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee e) {
        employees.add(e);
        e.setDepartment(this);
    }

    public void removeEmployee(Employee e) {
        employees.remove(e);
        e.setDepartment(null);
    }
}

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
```text
Hibernate: insert into user_profile (bio,phone) values (?,?)
Hibernate: insert into app_users (created_at,email,profile_id,username) values (?,?,?,?)
Hibernate: insert into department (name) values (?)
Hibernate: insert into employee (department_id,name,role) values (?,?,?)
Hibernate: insert into employee (department_id,name,role) values (?,?,?)
Hibernate: select count(*) from employee e1_0
Employees after Department save: 2
```

**Reflection question:**
_Which entity owns the `Department`–`Employee` relationship and why, and what value must `mappedBy` contain?_

Your answer:
The owning side of the relationship is the many side (Employee) as it has the foreign key column department_id on the database table. This means that the mappedBy on Department must equal department (as it is the Java field on the Employee class, not the DB column).

---

## Task 2 — Many-to-Many Join Table + Association Entity

**Approach / explanation:**
I initially set up a basic ManyToMany relationship between Student and Course by specifying the join table with the @JoinTable(name = "student_course", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id")) annotation on the Student side and mappedBy = "courses" on the Course side. I then created an association entity called Enrollment to allow additional fields to be stored. This contained the id, enrollmentDate, grade, and 2 ManyToOne relationships to Student and Course. Additionally, the Enrollment class used @Table(uniqueConstraints = @UniqueConstraint(name = "uk_student_course", columnNames = {"student_id", "course_id"})) to ensure that the same student can't be enrolled into the same course more than once.

**Code / endpoint(s) used (if applicable):**
```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();
}

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String title;

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();
}

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_student_course", columnNames = {"student_id", "course_id"}))
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate enrollmentDate;

    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
```text
Hibernate: create table student_course (student_id bigint not null, course_id bigint not null) engine=InnoDB
Hibernate: create table enrollment (id bigint not null auto_increment, enrollment_date date, grade varchar(255), course_id bigint not null, student_id bigint not null, primary key (id)) engine=InnoDB
Hibernate: alter table enrollment add constraint uk_student_course unique (student_id, course_id)
Hibernate: insert into student (name) values (?)
Hibernate: insert into course (code,title) values (?,?)
Hibernate: insert into enrollment (course_id,enrollment_date,grade,student_id) values (?,?,?,?)
Saved enrollment id: 1
Hibernate: insert into enrollment (course_id,enrollment_date,grade,student_id) values (?,?,?,?)
WARN  org.hibernate.orm.jdbc.error : Duplicate entry '1-1' for key 'enrollment.uk_student_course'
Duplicate enrollment rejected: Duplicate entry '1-1' for key 'enrollment.uk_student_course'
```

**Reflection question:**
_When must a many-to-many relationship be modeled as an association entity instead of a direct `@ManyToMany`, and what did the unique constraint protect in your test?_

Your answer:
If there are any extra attributes for a many-to-many relationship between two entities then it needs to be represented as an association entity instead of a direct @ManyToMany. In my example, the uk_student_course constraint was set up with a composite unique constraint stopping a student from appearing on the same course twice. This was done by throwing an error when trying to insert a second entry due to a duplicate entry.

---

## Task 3 — Fetch Strategies Experiment — LAZY vs EAGER

**Approach / explanation:**
I added FetchType.LAZY to the @ManyToOne relation of Employee.department. Then I created a service method without @Transactional that retrieved an Employee object and called employee.getDepartment().getName(), which resulted in a LazyInitializationException as the Hibernate session had been closed. This was solved in two ways. Firstly by adding @Transactional(readOnly = true) to the service method meaning that the session would remain open. Secondly by creating a custom query in the repository using JOIN FETCH e.department so both the Employee and Department are retrieved together.

**Code / endpoint(s) used (if applicable):**
```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.id = :id")
    Optional<Employee> findByIdWithDepartment(@Param("id") Long id);
}

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public String getDepartmentNameWithoutTransaction(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        return employee.getDepartment().getName();
    }

    @Transactional(readOnly = true)
    public String getDepartmentNameTransactional(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        return employee.getDepartment().getName();
    }

    public String getDepartmentNameFetchJoin(Long id) {
        Employee employee = employeeRepository.findByIdWithDepartment(id).orElseThrow();
        return employee.getDepartment().getName();
    }
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
```text
1. Lazy failure without @Transactional (1 SELECT query, then exception):
Hibernate: select e1_0.id,e1_0.department_id,e1_0.name,e1_0.role from employee e1_0 where e1_0.id=?
org.hibernate.LazyInitializationException: Could not initialize proxy [com.webdev2.model.Department#1] - no Session

2. Fix (a) with @Transactional (2 SELECT queries):
Hibernate: select e1_0.id,e1_0.department_id,e1_0.name,e1_0.role from employee e1_0 where e1_0.id=?
Hibernate: select d1_0.id,d1_0.name from department d1_0 where d1_0.id=?
Transactional fix dept: Engineering

3. Fix (b) with JOIN FETCH (1 SELECT query with join):
Hibernate: select e1_0.id,d1_0.id,d1_0.name,e1_0.name,e1_0.role from employee e1_0 join department d1_0 on d1_0.id=e1_0.department_id where e1_0.id=?
Fetch join fix dept: Engineering
```

Observation: Calling getDepartment().getName() without a transaction ran 1 SELECT query on Employee and then resulted in a LazyInitializationException. By using @Transactional on the method call we can successfully run 2 SELECT queries. Using JOIN FETCH resulted in only 1 SELECT query using a SQL JOIN. We avoid using EAGER on collections as it forces Hibernate to load the associated data on each query, causing unnecessary N+1 queries and slower performance.

**Reflection question:**
_What causes `LazyInitializationException`, and why is prefer-LAZY plus explicit fetch join/`@EntityGraph` preferable to marking everything EAGER?_

Your answer:
LazyInitializationException occurs if you try to access any lazily loaded relationships after the persistence context or transaction has been closed. It is best practice to use LAZY as default along with JOIN FETCH or @EntityGraph rather than marking everything as EAGER. This means we are not performing extra joins on our related tables when they are not needed and won't get N+1 performance issues around the application.

---

## Task 4 — Cascade + orphanRemoval — Safe vs. Dangerous

**Approach / explanation:**
In the Department class we have cascade = {CascadeType.PERSIST, CascadeType.MERGE} and orphanRemoval = true on employees. Since Department has a composition relationship with Employee, removing an Employee from the employees list will also delete the orphaned row. However in the Student class, cascade = {CascadeType.PERSIST, CascadeType.MERGE} was used without CascadeType.REMOVE and CascadeType.ALL. This is because the Course entity is a shared entity between multiple students, so when deleting a Student it does not delete the shared Course in the database.

**Code / endpoint(s) used (if applicable):**
```java
@OneToMany(mappedBy = "department", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
private List<Employee> employees = new ArrayList<>();

@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
)
private List<Course> courses = new ArrayList<>();
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
```text
(a) Orphan removal on Department.removeEmployee(emp2):
Employees before removeEmployee: 2
Hibernate: delete from employee where id=?
Employees after orphanRemoval: 1

(b) Shared Course survives Student delete:
Hibernate: insert into student (name) values (?)
Hibernate: insert into course (code,title) values (?,?)
Hibernate: insert into student_course (student_id,course_id) values (?,?)
Hibernate: delete from student_course where student_id=?
Hibernate: delete from student where id=?
Shared course still exists after student delete: true
```

**Reflection question:**
_What is the difference between `CascadeType.REMOVE` and `orphanRemoval = true`, and in which situation is each appropriate or dangerous?_

Your answer:
CascadeType.REMOVE will delete all child entries when the parent entity itself is deleted. Setting orphanRemoval = true will delete a child entry as soon as it is removed from the parent collection even if the parent is not deleted. Both of these are great for private child records that could not exist without the parent (such as order items). However they are both dangerous to use on shared entities such as Course as this will mean deleting one student or disconnecting the course will cause the course to be deleted for all students.

---

## Task 5 — Capstone Milestone 2 — ERD to JPA Entity Model

**Approach / explanation:**
I have created 4 JPA entities from the ERD of our veterinary clinic in Lab 8: Owner, Pet, Veterinarian, and Appointment. Owner and Pet have a bidirectional one-to-many relationship where the Pet entity is the owning side and contains the foreign key owner_id. Pet and Veterinarian have a many-to-many relationship with extra attributes (appointmentDate and reason), which I created using the association entity Appointment. It has 2 @ManyToOne relationships and foreign keys (pet_id and vet_id). All the relationships default to FetchType.LAZY. I have confirmed my model works with the repositories and a smoke test when running the application in Week9SmokeTest.

**Code / endpoint(s) used (if applicable):**
```java
@Entity
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

    public void addPet(Pet pet) {
        pets.add(pet);
        pet.setOwner(this);
    }
}

@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String microchipNumber;

    private String name;
    private String species;
    private String breed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();
}

@Entity
public class Veterinarian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String specialty;

    @OneToMany(mappedBy = "veterinarian", fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();
}

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate appointmentDate;
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vet_id", nullable = false)
    private Veterinarian veterinarian;
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**

ERD to JPA Entity Mapping Table:

| ERD Table | Entity Class | Primary Key | Relationship & Cardinality | Owning Side | FK / Join Column | Fetch & Cascade Justification |
|---|---|---|---|---|---|---|
| `owner` | `Owner` | `id` (`Long`) | `Owner` 1 to * `Pet` | `Pet` | `pet.owner_id` | `LAZY` (default for `@OneToMany`), `PERSIST/MERGE` + `orphanRemoval` so saving an owner saves their pets |
| `pet` | `Pet` | `id` (`Long`) | `ManyToOne` to `Owner`, `1 to *` to `Appointment` | `Pet` (for `Owner`), `Appointment` (for `Pet`) | `pet.owner_id`, `appointment.pet_id` | `LAZY` on `owner` to prevent unnecessary joins, `CascadeType.ALL` on `appointments` |
| `veterinarian` | `Veterinarian` | `id` (`Long`) | `Veterinarian` 1 to * `Appointment` | `Appointment` | `appointment.vet_id` | `LAZY` and no cascade delete because a veterinarian is shared across many pets |
| `appointment` | `Appointment` | `id` (`Long`) | Association entity for `Pet` * to * `Veterinarian` | `Appointment` | `pet_id`, `vet_id` | `LAZY` on both `@ManyToOne` fields so loading appointments does not eagerly join both tables |

Smoke-Test Output (`Week9SmokeTest`):

```text
Hibernate: insert into owner (name,phone) values (?,?)
Hibernate: insert into pet (breed,microchip_number,name,owner_id,species) values (?,?,?,?,?)
Hibernate: insert into veterinarian (name,specialty) values (?,?)
Hibernate: insert into appointment (appointment_date,pet_id,reason,vet_id) values (?,?,?,?)
Saved appointment id: 1 for pet Bantay
```

**Reflection question:**
_For one relationship in your capstone model, state the cardinality, the owning side, where the FK/join table lives, and why you chose that fetch and cascade setting._

Your answer:
The Owner and Pet mapping has a one-to-many cardinality as an owner can have many pets and each pet will only have one owner. Pet is the owning side and the foreign key column owner_id is in the pet table. I have set the fetch type of Pet.owner to LAZY so when querying pets it doesn't automatically join to the owner table. I have also set cascade to PERSIST and MERGE on Owner.pets so that when saving an owner it will also save any new pets that have been added to their list.

---

## Self-Check

- [x] All Week 9 work on the `week9` branch (project at branch root, no `week9/` folder)
- [x] This file completed and renamed `answers.md`
- [x] Repository link pasted into Moodle (no files uploaded)
