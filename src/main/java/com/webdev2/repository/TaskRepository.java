package com.webdev2.repository;

import com.webdev2.model.Task;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TaskRepository {
    private final List<Task> tasks = new ArrayList<>();
    private Long nextId = 1L;

    public TaskRepository() {
        Task t1 = new Task();
        t1.setId(nextId++);
        t1.setTitle("Study for WebDev2");
        t1.setDescription("Review Spring MVC and Thymeleaf");
        t1.setCompleted(false);
        tasks.add(t1);

        Task t2 = new Task();
        t2.setId(nextId++);
        t2.setTitle("Buy Groceries");
        t2.setDescription("Milk, eggs, bread");
        t2.setCompleted(true);
        tasks.add(t2);
    }

    public List<Task> findAll() {
        return tasks;
    }

    public Task findById(Long id) {
        for (Task t : tasks) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    public void save(Task task) {
        if (task.getId() == null) {
            task.setId(nextId++);
            tasks.add(task);
        }
    }
}