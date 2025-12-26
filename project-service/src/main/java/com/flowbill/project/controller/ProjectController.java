package com.flowbill.project.controller;

import com.flowbill.project.entity.Project;
import com.flowbill.project.entity.Task;
import com.flowbill.project.repository.ProjectRepository;
import com.flowbill.project.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectRepository.save(project);
    }

    @PostMapping("/{projectId}/tasks")
    public Task createTask(@PathVariable Long projectId, @RequestBody Task task) {
        Project p = projectRepository.findById(projectId).orElseThrow();
        task.setProject(p);
        return taskRepository.save(task);
    }
    
    @GetMapping("/tasks/{taskId}")
    public Task getTask(@PathVariable Long taskId) {
        return taskRepository.findById(taskId).orElseThrow();
    }
}
