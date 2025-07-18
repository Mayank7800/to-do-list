package com.todoapp.controller;

import com.todoapp.email.ReminderService;
import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ReminderService reminderService;

    // View all tasks
    @GetMapping
    public String getTasks(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("tasks", taskService.getUserTasks(user.getId()));
        return "view-tasks"; // Updated template for viewing tasks
    }

    // Show the add task form
    @GetMapping("/add")
    public String showAddTaskForm(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "add-task"; // Updated template for adding tasks
    }

    // Add a new task
    @PostMapping("/add")
    public String addTask(@ModelAttribute Task task, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            task.setUser(user);
            taskService.saveTask(task);
        }
        return "redirect:/tasks";
    }

    // Mark a task as completed
    @PostMapping("/complete")
    public String completeTask(@RequestParam("taskId") Long taskId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            taskService.markTaskAsComplete(taskId, user.getId());
        }
        return "redirect:/tasks";
    }


    // Manually trigger email reminders
    @RequestMapping(value = "/sendReminders", method = {RequestMethod.GET, RequestMethod.POST})
    public String sendEmailReminders() {
        reminderService.sendReminders();
        return "redirect:/tasks";
    }
 // Logout and invalidate the session
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate the session to log out the user
        return "redirect:/login"; // Redirect to login page
    }
}

