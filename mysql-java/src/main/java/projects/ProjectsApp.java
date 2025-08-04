package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

// ANCHOR This file is where all of the user inputs for CRUD operations in my menu app are handled, with each portion labeled by operation. I've included Javadoc tags in all other code files for documentation purposes
public class ProjectsApp {
  private Scanner scanner = new Scanner(System.in); // Reads user input
  private ProjectService projectService = new ProjectService(); // Handles project services
  private Project curProject; // Current selected project

  // @formatter:off
  private List<String> operations = List.of(
      "1) Add a project",
      "2) List projects",
      "3) Select a project",
      "4) Update project details",
      "5) Delete a project"
  );
  // @formatter:on

  // App entry point
  public static void main(String[] args) {
    new ProjectsApp().processUserSelections(); 
  }

  // Process user menu choices
  private void processUserSelections() {
    boolean done = false; 

    while (!done) {
      try {
        int selection = getUserSelection(); 

        switch (selection) {
          case 1:
            createProject(); 
            break;

          case 2:
            listProjects(); 
            break;

          case 3:
            selectProject(); 
            break;

          case 4:
            updateProjectDetails(); 
            break;

          case 5:
            deleteProject(); 
            break;

          default:
            System.out.println("" + selection + " isn't valid. Try again"); 
            break;
        }
      } catch (Exception e) {
        System.out.println("Error: " + e + " Try again"); 
      }
    }
  }

  // Update details of current project
  private void updateProjectDetails() {
    if (Objects.isNull(curProject)) {
      System.out.println("Choose a project first"); 
      return;
    }
    // Prompt for updates
    String projectName = getStringInput("Enter project name (" + curProject.getProjectName() + ")"); 
    BigDecimal estimatedHours = getDecimalInput("Enter estimated hours (" + curProject.getEstimatedHours() + ")"); 
    BigDecimal actualHours = getDecimalInput("Enter actual hours (" + curProject.getActualHours() + ")"); 
    Integer difficultyInput = getIntInput("Enter difficulty (1-5) (" + curProject.getDifficulty() + ")"); 
Integer difficulty = null;
if (difficultyInput != null) {
  if (difficultyInput < 1 || difficultyInput > 5) {
    System.out.println("Difficulty must be between 1 and 5. Keeping original value.");
  } else {
    difficulty = difficultyInput;
  }
} else {
  difficulty = curProject.getDifficulty();
} 
    String notes = getStringInput("Enter note (" + curProject.getNotes() + ")"); 

    // Set new values if provided
    curProject.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
    curProject.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
    curProject.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
    curProject.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
    curProject.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);

    projectService.updateProject(curProject);
    curProject = projectService.fetchProjectById(curProject.getProjectId()); 
    System.out.println("Project updated successfully"); 
  }

  // Delete current project
  private void deleteProject() {
    if (Objects.isNull(curProject)) {
      System.out.println("Please choose a project first"); 
      return;
    }
    Integer input = getIntInput("Are you sure? Enter 1 to delete project " + curProject.getProjectId()); 
    if (input != null && input == 1) {
      projectService.deleteProject(curProject.getProjectId()); 
      curProject = null; 
      System.out.println("Project deleted");
    }
  }

  // Select a project
  private void selectProject() {
    listProjects(); 
    Integer projectId = getIntInput("Enter project ID"); 
    curProject = null; 
    curProject = projectService.fetchProjectById(projectId);
  }

  // List all projects
  private void listProjects() {
    List<Project> projects = projectService.fetchAllProjects(); 
    System.out.println("Projects:"); 
    projects.forEach(project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName())); 
  }

  // Create new project
  private void createProject() {
    String projectName = getStringInput("Enter project name"); 
    BigDecimal estimatedHours = getDecimalInput("Enter estimated hours"); 
    BigDecimal actualHours = getDecimalInput("Enter actual hours"); 
    Integer difficulty = null;
while (difficulty == null) {
  Integer input = getIntInput("Enter difficulty (1-5)");
  if (input != null && (input < 1 || input > 6)) {
    System.out.println("Difficulty must be between 1 and 5. Try again.");
  } else {
    difficulty = input;
  }
} 
    String notes = getStringInput("notes"); 

    Project project = new Project(); 
    project.setProjectName(projectName);
    project.setEstimatedHours(estimatedHours);
    project.setActualHours(actualHours);
    project.setDifficulty(difficulty);
    project.setNotes(notes);

    Project dbProject = projectService.addProject(project); 
    System.out.println("Created: " + dbProject); 
  }

  // Get decimal input
  private BigDecimal getDecimalInput(String prompt) {
    String input = getStringInput(prompt); 
    if (Objects.isNull(input)) return null;
    try {
      return new BigDecimal(input).setScale(2);
    } catch (NumberFormatException e) {
      throw new DbException(input + " invalid number"); 
    }
  }

  // Get user menu choice
  private int getUserSelection() {
    while (true) {
      printOperations(); 
      Integer input = getIntInput("Enter selection"); 
      if (input != null && input >= 1 && input <= 5) {
        return input;
      } else {
        System.out.println("Invalid selection. Enter a number between 1 and 5.");
      }
    }
  }

  // Get integer input
  private Integer getIntInput(String prompt) {
    while (true) {
      String input = getStringInput(prompt); 
      if (input == null || input.isBlank()) {
        System.out.println("Invalid input. Please try again.");
        continue;
      }
      try {
        return Integer.valueOf(input); 
      } catch (NumberFormatException e) {
        System.out.println(input + " invalid number. Please try again."); 
      }
    }
  }

  // Get string input
  private String getStringInput(String prompt) {
    System.out.print(prompt + ": "); 
    String input = scanner.nextLine(); 
    return input.isBlank() ? null : input.trim(); 
  }

  // Print menu options
  private void printOperations() {
    System.out.println("Selections (Ctrl+C to Quit):"); 
    operations.forEach(line -> System.out.println("  " + line)); 
    System.out.println(Objects.isNull(curProject) ? "No project chosen" : "Working on: " + curProject); 
  }
}