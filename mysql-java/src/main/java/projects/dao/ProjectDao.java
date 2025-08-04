package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

// This handles project CRUD operations with JDBC

public class ProjectDao extends DaoBase {
  private static final String PROJECT_TABLE = "project";

  /**
   * Inserts a new project
   * @param project Project to save
   * @return Project with assigned ID
   */
  public Project insertProject(Project project) {
    // @formatter:off
    String sql = ""
        + "INSERT INTO " + PROJECT_TABLE + " "
        + "(project_name, estimated_hours, actual_hours, difficulty, notes) "
        + "VALUES "
        + "(?, ?, ?, ?, ?)";
    // @formatter:on

    try(Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);

      try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, project.getProjectName(), String.class);
        setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
        setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
        setParameter(stmt, 4, project.getDifficulty(), Integer.class);
        setParameter(stmt, 5, project.getNotes(), String.class);

        stmt.executeUpdate();

        Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
        commitTransaction(conn);

        project.setProjectId(projectId);
        return project;
      }
      catch(Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    }
    catch(SQLException e) {
      throw new DbException(e);
    }
  }

  /**
   * Fetches all projects, sorted by name
   * @return List of projects
   */
  public List<Project> fetchAllProjects() {
    String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

    try(Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);

      try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        try(ResultSet rs = stmt.executeQuery()) {
          List<Project> projects = new LinkedList<>();

          while(rs.next()) {
            projects.add(extract(rs, Project.class));
          }

          return projects;
        }
      }
      catch(Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    }
    catch(SQLException e) {
      throw new DbException(e);
    }
  }

  /**
   * Fetches project by ID
   * @param projectId Project ID
   * @return Optional project
   */
  public Optional<Project> fetchProjectById(Integer projectId) {
    String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";

    try(Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);

      try {
        Project project = null;

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
          setParameter(stmt, 1, projectId, Integer.class);

          try(ResultSet rs = stmt.executeQuery()) {
            if(rs.next()) {
              project = extract(rs, Project.class);
            }
          }
        }

        commitTransaction(conn);
        return Optional.ofNullable(project);
      }
      catch(Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    }
    catch(SQLException e) {
      throw new DbException(e);
    }
  }

  /**
   * Updates project details
   * @param project Project with updates
   * @return True if update worked
   */
  public boolean updateProject(Project project) {
    String sql = "UPDATE " + PROJECT_TABLE + " SET project_name = ?, estimated_hours = ?, actual_hours = ?, difficulty = ?, notes = ? WHERE project_id = ?";

    try (Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);

      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, project.getProjectName(), String.class);
        setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
        setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
        setParameter(stmt, 4, project.getDifficulty(), Integer.class);
        setParameter(stmt, 5, project.getNotes(), String.class);
        setParameter(stmt, 6, project.getProjectId(), Integer.class);

        boolean updated = stmt.executeUpdate() > 0;
        commitTransaction(conn);
        return updated;
      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }

  /**
   * Deletes project
   * @param projectId Project ID
   * @return True if deletion worked
   */
  public boolean deleteProject(Integer projectId) {
    try (Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);

      try {
        String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
          setParameter(stmt, 1, projectId, Integer.class);
          boolean deleted = stmt.executeUpdate() > 0;
          commitTransaction(conn);

          String countSql = "SELECT COUNT(*) FROM " + PROJECT_TABLE;
          try (PreparedStatement countStmt = conn.prepareStatement(countSql);
               ResultSet rs = countStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
              String resetSql = "ALTER TABLE " + PROJECT_TABLE + " AUTO_INCREMENT = 1";
              try (PreparedStatement resetStmt = conn.prepareStatement(resetSql)) {
                resetStmt.executeUpdate();
              }
            }
          }

          return deleted;
        }
      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }
}