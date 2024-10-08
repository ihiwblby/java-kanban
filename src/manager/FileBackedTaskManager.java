package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import utility.Status;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    public static final String TITLE = "id,type,name,status,description,epic,duration,startTime\n";

    public FileBackedTaskManager(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Файл не может быть null");
        }
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(TITLE);

            getAllTasks().stream()
                    .map(Task::toStringForFile)
                    .forEach(taskString -> {
                        try {
                            writer.write(taskString + "\n");
                        } catch (IOException exp) {
                            throw new ManagerSaveException("Ошибка при сохранении задачи");
                        }
                    });

            getAllEpics().stream()
                    .map(Epic::toStringForFile)
                    .forEach(epicString -> {
                        try {
                            writer.write(epicString + "\n");
                        } catch (IOException exp) {
                            throw new ManagerSaveException("Ошибка при сохранении эпика");
                        }
                    });

            getAllSubtasks().stream()
                    .map(Subtask::toStringForFile)
                    .forEach(subtaskString -> {
                        try {
                            writer.write(subtaskString + "\n");
                        } catch (IOException exp) {
                            throw new ManagerSaveException("Ошибка при сохранении подзадачи");
                        }
                    });

        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int idCounter = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();

            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isEmpty()) {
                    continue;
                }

                Task task = manager.fromString(line);
                int taskId = task.getId();
                if (taskId > idCounter) {
                    idCounter = taskId;
                }

                switch (task.getType()) {
                    case TASK -> manager.tasks.put(taskId, task);
                    case EPIC -> manager.epics.put(taskId, (Epic) task);
                    case SUBTASK -> manager.subtasks.put(taskId, (Subtask) task);
                    default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + task.getType());
                }
            }

        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                return manager;
            }
            throw new ManagerSaveException("Ошибка при загрузке данных из файла: " + e.getMessage());
        }

        manager.idCounter = idCounter + 1;
        manager.getAllEpics().forEach(manager::recalculateEpicTimeAndDuration);
        return manager;
    }

    public Task fromString(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Переданная строка пустая или null.");
        }

        String[] element = value.split(",");
        int id = Integer.parseInt(element[0]);
        String taskType = element[1];
        String name = element[2];
        Status status = Status.valueOf(element[3]);
        String description = element[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(element[6]));
        LocalDateTime startTime = LocalDateTime.parse(element[7]);

        switch (taskType) {
            case "SUBTASK" -> {
                int myEpicId = Integer.parseInt(element[5]);
                Subtask subtask = new Subtask(name, description, myEpicId, duration, startTime);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
            }
            case "EPIC" -> {
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            }
            case "TASK" -> {
                Task task = new Task(name, description, duration, startTime);
                task.setId(id);
                task.setStatus(status);
                return task;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + taskType);
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        super.updateEpicStatus(epicId);
        save();
    }
}
