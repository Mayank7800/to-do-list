package com.todoapp.email;

import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.repository.TaskRepository;
import com.todoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ReminderService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository; // To get user emails

    @Scheduled(cron = "0 0 9 * * ?") // Runs every day at 9 AM
    public void sendReminders() {
        List<Task> tasks = taskRepository.findAll();

        for (Task task : tasks) {
            if (task.getDeadline() != null && !task.isCompleted()) {
                long daysLeft = TimeUnit.DAYS.toDays(java.sql.Date.valueOf(task.getDeadline()).getTime() - System.currentTimeMillis());

                if (daysLeft > 0 && daysLeft <= 2) { // If due date is within 2 days
                    // Fetch the user associated with the task
                    User user = task.getUser();

                    if (user != null) {
                        // Send reminder email to the user
                        emailService.sendEmail(
                                user.getEmail(), // Send to the user's email
                                "Task Due Reminder",
                                "Your task '" + task.getTitle() + "' is due in " + daysLeft + " days."
                        );
                    }
                }
            }
        }
    }
}
