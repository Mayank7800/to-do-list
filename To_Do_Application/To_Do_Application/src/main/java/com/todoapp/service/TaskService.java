package com.todoapp.service;

import com.todoapp.email.EmailService;
import com.todoapp.model.Task;
import com.todoapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;

    public List<Task> getUserTasks(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    public Task saveTask(Task task) {
        // Send email notification for high-priority tasks
        if (task.getPriority() == Task.Priority.HIGH) {
            emailService.sendEmail(
                    task.getUser().getEmail(),
                    "High Priority Task Added",
                    "Task: " + task.getTitle() + "\nDeadline: " + task.getDeadline()
            );
        }
        return taskRepository.save(task);
    }

    public void markTaskAsComplete(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null && task.getUser().getId().equals(userId)) {
            task.setCompleted(true);
            taskRepository.save(task);
        }
    }


	
}
