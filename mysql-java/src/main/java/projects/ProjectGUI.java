package projects;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.math.BigDecimal;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import projects.entity.Project;
import projects.service.ProjectService;

// Experimental GUI for this project
public class ProjectGUI extends JFrame {
    private ProjectService projectService = new ProjectService();
    private JTable projectTable;
    private DefaultTableModel tableModel;
    private Project selectedProject;

    public ProjectGUI() {
        setTitle("Project Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columns = {"ID", "Name", "Est. Hours", "Act. Hours", "Difficulty", "Notes"};
        tableModel = new DefaultTableModel(columns, 0);
        projectTable = new JTable(tableModel);
        add(new JScrollPane(projectTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Project");
        JButton updateButton = new JButton("Update Project");
        JButton deleteButton = new JButton("Delete Project");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addProject());
        updateButton.addActionListener(e -> updateProject());
        deleteButton.addActionListener(e -> deleteProject());
        refreshButton.addActionListener(e -> loadProjects());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadProjects();
    }

    private void loadProjects() {
        tableModel.setRowCount(0);
        List<Project> projects = projectService.fetchAllProjects();
        for (Project p : projects) {
            tableModel.addRow(new Object[]{p.getProjectId(), p.getProjectName(), p.getEstimatedHours(), p.getActualHours(), p.getDifficulty(), p.getNotes()});
        }
    }

    private void addProject() {
        Project project = showProjectDialog(null);
        if (project != null) {
            projectService.addProject(project);
            loadProjects();
        }
    }

    private void updateProject() {
        int row = projectTable.getSelectedRow();
        if (row >= 0) {
            Integer id = (Integer) tableModel.getValueAt(row, 0);
            selectedProject = projectService.fetchProjectById(id);
            Project updated = showProjectDialog(selectedProject);
            if (updated != null) {
                projectService.updateProject(updated);
                loadProjects();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a project to update.");
        }
    }

    private void deleteProject() {
        int row = projectTable.getSelectedRow();
        if (row >= 0) {
            Integer id = (Integer) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete project " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                projectService.deleteProject(id);
                loadProjects();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a project to delete.");
        }
    }

    private Project showProjectDialog(Project existing) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField nameField = new JTextField(20);
        JTextField estHoursField = new JTextField(20);
        JTextField actHoursField = new JTextField(20);
        JTextField difficultyField = new JTextField(20);
        JTextField notesField = new JTextField(20);

        if (existing != null) {
            nameField.setText(existing.getProjectName());
            estHoursField.setText(existing.getEstimatedHours().toString());
            actHoursField.setText(existing.getActualHours().toString());
            difficultyField.setText(existing.getDifficulty().toString());
            notesField.setText(existing.getNotes());
        }

        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Est. Hours:"), gbc);
        gbc.gridx = 1;
        panel.add(estHoursField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Act. Hours:"), gbc);
        gbc.gridx = 1;
        panel.add(actHoursField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Difficulty (1-5):"), gbc);
        gbc.gridx = 1;
        panel.add(difficultyField, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        panel.add(notesField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, existing == null ? "Add Project" : "Update Project", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Project p = existing != null ? existing : new Project();
                p.setProjectName(nameField.getText());
                p.setEstimatedHours(new BigDecimal(estHoursField.getText()));
                p.setActualHours(new BigDecimal(actHoursField.getText()));
                int diff = Integer.parseInt(difficultyField.getText());
                if (diff < 1 || diff > 5) throw new NumberFormatException();
                p.setDifficulty(diff);
                p.setNotes(notesField.getText());
                return p;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage());
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProjectGUI().setVisible(true));
    }
}