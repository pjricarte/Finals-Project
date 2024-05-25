# Task Manager Application

## Overview

The Task Manager is a Java-based desktop application designed to help students manage their tasks. It features a user-friendly interface for adding, viewing, completing, and deleting tasks. Users can also set up a passcode for secure access to their task list.

## Features

- **Passcode Setup and Authentication**: Users can set up a 6-digit passcode to secure access to their tasks.
- **Task Management**: Users can add new tasks, mark tasks as complete, view task descriptions, and delete tasks.
- **Task Details**: Each task includes a name, description, deadline date, and time.
- **Data Persistence**: Tasks are saved to a file and loaded when the application starts.

## Requirements

- Java Development Kit (JDK) 8 or higher

## Setup and Usage

1. **Compile the Code**: Open a terminal and navigate to the directory containing the source code files. Compile the code using the following command:
    ```sh
    javac TaskManager.java
    ```

2. **Run the Application**: After compilation, run the application using:
    ```sh
    java TaskManager
    ```

## Code Structure

### Main Class: `TaskManager`

- **Attributes**:
  - `PASSCODE_FILE`: The file where the passcode is stored.
  - `TASKS_FILE`: The file where tasks are stored.
  - `tasks`: A list to store task objects.
  - `taskListModel`: A model for the task list UI component.
  - `isPasscodeSet`: A boolean indicating if a passcode is set.

- **Methods**:
  - `TaskManager()`: Constructor to initialize the task manager.
  - `initComponents()`: Initializes the main components of the application.
  - `showWelcomePage()`: Displays the welcome page with options to set up a passcode or log in.
  - `setupPasscode()`: Allows the user to set up a new passcode.
  - `authenticateUser()`: Authenticates the user based on the stored passcode.
  - `showTaskManager()`: Displays the task management interface.
  - `addTask()`: Prompts the user to add a new task.
  - `markAsComplete(int index)`: Marks a selected task as complete.
  - `deleteTask(int index)`: Deletes a selected task.
  - `showTaskDescription(int index)`: Shows the description of a selected task.
  - `loadTasks()`: Loads tasks from a file.
  - `saveTasks()`: Saves tasks to a file.
  - `saveTasksAndExit()`: Saves tasks and exits the application.
  - `generateNumbers(int start, int end)`: Generates an array of integers for UI components.

### Supporting Class: `Task`

- **Attributes**:
  - `name`: The name of the task.
  - `description`: The description of the task.
  - `deadlineDate`: The deadline date of the task in `yyyy-MM-dd` format.
  - `deadlineTime`: The deadline time of the task in `HH:mm` format.
  - `isComplete`: A boolean indicating if the task is complete.

- **Methods**:
  - `Task(String name, String description, String deadlineDate, String deadlineTime)`: Constructor to create a new task.
  - `getName()`, `getDescription()`, `getDeadlineDate()`, `getDeadlineTime()`, `isComplete()`: Getter methods.
  - `setComplete(boolean complete)`: Sets the task's completion status.
  - `toString()`: Returns a string representation of the task.
  - `formatDate(String date)`: Formats the date for display.
  - `formatTime(String time)`: Formats the time for display.

## Event-Driven Programming Examples

### Example 1: Handling Button Clicks to Setup Passcode

```java
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
```

### Example 2: Handling Mouse Clicks on the Task List

```java
taskList.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            int index = taskList.locationToIndex(evt.getPoint());
            showTaskDescription(index);
        }
    }
});
```

### Example 3: Handling Button Clicks to Manage Tasks

```java
addButton.addActionListener(e -> addTask());
markCompleteButton.addActionListener(e -> markAsComplete(taskList.getSelectedIndex()));
deleteButton.addActionListener(e -> deleteTask(taskList.getSelectedIndex()));
exitButton.addActionListener(e -> saveTasksAndExit());
```

## Notes

- Ensure the `passcode.txt` and `tasks.txt` files are in the same directory as the compiled `.class` files for data persistence.
- The application uses `JOptionPane` for dialog interactions.

## License

This project is licensed under the MIT License.

---

This README provides an overview, setup instructions, code structure, and examples of event-driven programming in the Task Manager application. For any issues or contributions, feel free to open a pull request or report an issue on the project repository.