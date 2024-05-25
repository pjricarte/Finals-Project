import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StudentManagementSystem extends JFrame {

    private static final String PASSCODE_FILE = "passcode.txt";
    private static final String TASKS_FILE = "tasks.txt";
    private ArrayList<Task> tasks;
    private DefaultListModel<String> taskListModel;
    private boolean isPasscodeSet = false;

    public StudentManagementSystem() {
        tasks = new ArrayList<>();
        taskListModel = new DefaultListModel<>();
        initComponents();
        loadTasks();
    }

    private void initComponents() {
        setTitle("Task Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showWelcomePage();
    }

    private void showWelcomePage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 0, 128));
        JLabel welcomeLabel = new JLabel("Welcome to Student Task Manager!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        panel.add(welcomeLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0, 0, 128));
        JButton setupPasscodeButton = new JButton("Setup Passcode");
        JButton loginButton = new JButton("Login");
        setupPasscodeButton.setForeground(Color.BLACK);
        loginButton.setForeground(Color.BLACK);
        buttonPanel.add(setupPasscodeButton);
        buttonPanel.add(loginButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        setupPasscodeButton.addActionListener(e -> {
            remove(panel);
            setupPasscode();
        });

        loginButton.addActionListener(e -> {
            if (authenticateUser()) {
                remove(panel);
                showTaskManager();
            }
        });

        setVisible(true);
    }

    private void setupPasscode() {
        while (true) {
            JPasswordField passcodeField = new JPasswordField();
            JPasswordField confirmField = new JPasswordField();
            Object[] message = {
                    "Set a 6-digit passcode:", passcodeField,
                    "Confirm passcode:", confirmField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Setup Passcode", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String passcode = new String(passcodeField.getPassword());
                String confirmPasscode = new String(confirmField.getPassword());
                if (passcode.length() == 6 && passcode.equals(confirmPasscode)) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSCODE_FILE))) {
                        writer.write(passcode);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isPasscodeSet = true;
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "Passcodes do not match or are not 6 digits. Try again.");
                }
            } else {
                break;
            }
        }
        if (isPasscodeSet) {
            showTaskManager();
        }
    }

    private boolean authenticateUser() {
        String storedPasscode = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(PASSCODE_FILE))) {
            storedPasscode = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            JPasswordField passwordField = new JPasswordField();
            Object[] message = {
                    "Enter 6-digit passcode:", passwordField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String passcode = new String(passwordField.getPassword());
                if (passcode.equals(storedPasscode)) {
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid passcode! Try again.");
                }
            } else {
                return false;
            }
        }
    }

    private void showTaskManager() {
        getContentPane().removeAll();
        revalidate();
        repaint();

        JPanel panel = new JPanel(new BorderLayout());
        JList<String> taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(taskList), BorderLayout.CENTER);

        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = taskList.locationToIndex(evt.getPoint());
                    showTaskDescription(index);
                }
            }
        });

        JPanel buttonsPanel = new JPanel();
        JButton addButton = new JButton("Add Task");
        JButton markCompleteButton = new JButton("Mark As Complete");
        JButton deleteButton = new JButton("Delete Task");
        JButton exitButton = new JButton("Exit");

        buttonsPanel.add(addButton);
        buttonsPanel.add(markCompleteButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(exitButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        add(panel);

        addButton.addActionListener(e -> addTask());
        markCompleteButton.addActionListener(e -> markAsComplete(taskList.getSelectedIndex()));
        deleteButton.addActionListener(e -> deleteTask(taskList.getSelectedIndex()));
        exitButton.addActionListener(e -> saveTasksAndExit());

        setVisible(true);
    }

    private void addTask() {
        JTextField taskNameField = new JTextField();
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JComboBox<String> monthBox = new JComboBox<>(new String[] {
                "January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
                "November", "December"
        });
        JComboBox<Integer> dayBox = new JComboBox<>(generateNumbers(1, 31));
        JComboBox<Integer> yearBox = new JComboBox<>(generateNumbers(2024, 2030));

        JComboBox<Integer> hourBox = new JComboBox<>(generateNumbers(1, 12));
        JComboBox<Integer> minuteBox = new JComboBox<>(generateNumbers(0, 59));
        JComboBox<String> amPmBox = new JComboBox<>(new String[] { "AM", "PM" });

        Object[] message = {
                "Task Name:", taskNameField,
                "Description:", new JScrollPane(descriptionArea),
                "Deadline Date:", monthBox, dayBox, yearBox,
                "Deadline Time:", hourBox, minuteBox, amPmBox
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add New Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String taskName = taskNameField.getText();
            String description = descriptionArea.getText();
            int month = monthBox.getSelectedIndex();
            int day = (int) dayBox.getSelectedItem();
            int year = (int) yearBox.getSelectedItem();

            int hour = (int) hourBox.getSelectedItem();
            int minute = (int) minuteBox.getSelectedItem();
            String amPm = (String) amPmBox.getSelectedItem();

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            if ("PM".equals(amPm) && hour != 12) {
                hour += 12;
            }
            if ("AM".equals(amPm) && hour == 12) {
                hour = 0;
            }
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String formattedDate = dateFormat.format(calendar.getTime());
            String formattedTime = timeFormat.format(calendar.getTime());

            Task newTask = new Task(taskName, description, formattedDate, formattedTime);
            tasks.add(newTask);
            taskListModel.addElement(newTask.toString());
            saveTasks();
        }
    }

    private void markAsComplete(int index) {
        if (index >= 0 && index < tasks.size()) {
            Task task = tasks.get(index);
            task.setComplete(true);
            taskListModel.set(index, task.toString());
            saveTasks();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to mark as complete.");
        }
    }

    private void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            taskListModel.remove(index);
            saveTasks();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.");
        }
    }

    private void showTaskDescription(int index) {
        if (index >= 0 && index < tasks.size()) {
            Task task = tasks.get(index);
            JOptionPane.showMessageDialog(this, task.getDescription(), "Task Description",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TASKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length == 5) {
                    Task task = new Task(parts[0], parts[1], parts[2], parts[3]);
                    task.setComplete(Boolean.parseBoolean(parts[4]));
                    tasks.add(task);
                    taskListModel.addElement(task.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TASKS_FILE))) {
            for (Task task : tasks) {
                writer.write(task.getName() + ";" + task.getDescription() + ";" + task.getDeadlineDate() + ";"
                        + task.getDeadlineTime() + ";" + task.isComplete());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTasksAndExit() {
        saveTasks();
        System.exit(0);
    }

    private Integer[] generateNumbers(int start, int end) {
        Integer[] numbers = new Integer[end - start + 1];
        for (int i = start; i <= end; i++) {
            numbers[i - start] = i;
        }
        return numbers;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentManagementSystem::new);
    }
}

class Task {
    private String name;
    private String description;
    private String deadlineDate;
    private String deadlineTime;
    private boolean isComplete;

    public Task(String name, String description, String deadlineDate, String deadlineTime) {
        this.name = name;
        this.description = description;
        this.deadlineDate = deadlineDate;
        this.deadlineTime = deadlineTime;
        this.isComplete = false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDeadlineDate() {
        return deadlineDate;
    }

    public String getDeadlineTime() {
        return deadlineTime;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public String toString() {
        String displayDate = formatDate(deadlineDate);
        String displayTime = formatTime(deadlineTime);
        String displayText = name + "\n - Deadline on " + displayDate + "\n - at " + displayTime;
        if (isComplete) {
            displayText += "\n(Completed)";
        }
        return displayText;
    }

    private String formatDate(String date) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = originalFormat.parse(date);
            SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM dd, yyyy");
            return displayFormat.format(parsedDate);
        } catch (Exception e) {
            return date; // fallback in case of parsing error
        }
    }

    private String formatTime(String time) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("HH:mm");
            Date parsedTime = originalFormat.parse(time);
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
            return displayFormat.format(parsedTime);
        } catch (Exception e) {
            return time;
        }
    }
}