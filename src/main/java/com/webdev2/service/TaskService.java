package com.webdev2.service;

import com.webdev2.model.Task;
import com.webdev2.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public Task getTaskById(Long id) {
        return repository.findById(id);
    }

    public void saveTask(Task task) {
        repository.save(task);
    }
}