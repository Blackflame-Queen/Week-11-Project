package projects;
import javax.swing.SwingUtilities;

// Main entry point for the application
public class ProjectsApp {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ProjectGUI().setVisible(true));
  }
}