package projects.entity;

import java.math.BigDecimal;

// This is what represents a project in the menu, with a list of details and values
public class Project {
  private Integer projectId;
  private String projectName;
  private BigDecimal estimatedHours;
  private BigDecimal actualHours;
  private Integer difficulty;
  private String notes;

  /**
   * Gets project ID
   * @return Project ID
   */
  public Integer getProjectId() {
    return projectId;
  }

  /**
   * Sets project ID
   * @param projectId Project ID
   */
  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  /**
   * Gets project name
   * @return Project name
   */
  public String getProjectName() {
    return projectName;
  }

  /**
   * Sets project name
   * @param projectName Project name
   */
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  /**
   * Gets estimated hours
   * @return Estimated hours
   */
  public BigDecimal getEstimatedHours() {
    return estimatedHours;
  }

  /**
   * Sets estimated hours
   * @param estimatedHours Estimated hours
   */
  public void setEstimatedHours(BigDecimal estimatedHours) {
    this.estimatedHours = estimatedHours;
  }

  /**
   * Gets actual hours
   * @return Actual hours
   */
  public BigDecimal getActualHours() {
    return actualHours;
  }

  /**
   * Sets actual hours
   * @param actualHours Actual hours
   */
  public void setActualHours(BigDecimal actualHours) {
    this.actualHours = actualHours;
  }

  /**
   * Gets difficulty level
   * @return Difficulty level
   */
  public Integer getDifficulty() {
    return difficulty;
  }

  /**
   * Sets difficulty level
   * @param difficulty Difficulty level
   */
  public void setDifficulty(Integer difficulty) {
    this.difficulty = difficulty;
  }

  /**
   * Gets project notes
   * @return Project notes
   */
  public String getNotes() {
    return notes;
  }

  /**
   * Sets project notes
   * @param notes Project notes
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * Builds project overview
   * @return Formatted project details
   */
  @Override
  public String toString() {
    String result = "";
    
    result += "\n   ID= " + projectId;
    result += "\n   Project Name= " + projectName;
    result += "\n   Estimated Hours= " + estimatedHours;
    result += "\n   Actual Hours= " + actualHours;
    result += "\n   Difficulty= " + difficulty;
    result += "\n   Notes= " + notes;
    
    return result;
  }
}