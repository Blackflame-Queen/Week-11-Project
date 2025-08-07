package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import projects.dao.ProjectDao;
import projects.entity.Project;

// This file serves to manage project CRUD tasks
public class ProjectService {
  private ProjectDao projectDao = new ProjectDao();

  /**
   * Adds a new project
   * @param project Project to save
   * @return New project with its ID
   */
  public Project addProject(Project project) {
    return projectDao.insertProject(project);
  }

  /**
   * Gathers all projects
   * @return List of all projects
   */
  public List<Project> fetchAllProjects() {
    return projectDao.fetchAllProjects();
  }

  /**
   * Finds project by ID
   * @param projectId Project ID to look up
   * @return Project or throws error
   * @throws NoSuchElementException If project not found
   */
  public Project fetchProjectById(Integer projectId) {
    return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException(
        "Project with ID= " + projectId + " does not exist."));
  }

  /**
   * Updates project details
   * @param project Project with new info
   * @return True if update worked
   * @throws NoSuchElementException If project not found
   */
  public boolean updateProject(Project project) {
    if (fetchProjectById(project.getProjectId()) == null) {
      throw new NoSuchElementException("Project with ID=" + project.getProjectId() + " does not exist.");
    }
    return projectDao.updateProject(project);
  }

  /**
   * Removes project by ID
   * @param projectId Project ID to delete
   * @return True if deletion worked
   * @throws NoSuchElementException If project not found
   */
  public boolean deleteProject(Integer projectId) {
    if (fetchProjectById(projectId) == null) {
      throw new NoSuchElementException("Project with ID=" + projectId + " does not exist.");
    }
    return projectDao.deleteProject(projectId);
  }
}