package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import utility.Status;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Файл не может быть null");
        }
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(task.toStringForFile() + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(epic.toStringForFile() + "\n");

                for (Subtask subtask : getSubtasksByEpicId(epic.getId())) {
                    writer.write(subtask.toStringForFile() + "\n");
                }
            }

        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int idCounter = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();

            while ((reader.ready())) {
                String line = reader.readLine();
                if (line.isEmpty()) {
                    break;
                }

                Task task = manager.fromString(line);
                if (task.getId() > idCounter) {
                    idCounter = task.getId();
                }

                switch (task.getType()) {
                    case TASK -> manager.tasks.put(task.getId(), task);
                    case EPIC -> manager.epics.put(task.getId(), (Epic) task);
                    case SUBTASK -> manager.subtasks.put(task.getId(), (Subtask) task);
                    default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + task.getType());
                }
            }

        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                return manager;
            }
            throw new ManagerSaveException("Ошибка при загрузке данных из файла: " + e.getMessage());
        }

        for (Subtask subtask : manager.subtasks.values()) {
            Epic epic = manager.epics.get(subtask.getMyEpicId());
            if (epic != null) {
                epic.addSubtaskId(subtask.getId());
                manager.updateEpic(epic);
            } else {
                throw new IllegalStateException("Эпик с ID " + subtask.getMyEpicId() + " не найден для подзадачи с ID "
                        + subtask.getId());
            }
        }

        manager.idCounter = idCounter + 1;
        return manager;
    }

    private Task fromString(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Переданная строка пустая или null.");
        }

        String[] element = value.split(",");
        int id = Integer.parseInt(element[0]);
        String taskType = element[1];
        String name = element[2];
        Status status = Status.valueOf(element[3]);
        String description = element[4];

        switch (taskType) {
            case "SUBTASK" -> {
                int myEpicId = Integer.parseInt(element[5]);
                Subtask subtask = new Subtask(name, description, myEpicId);
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
                Task task = new Task(name, description);
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
