import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idCounter = 0;

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            updateEpicStatus(epic.getId());
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void createTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getMyEpicId())) {
            subtask.setId(idCounter++);
            subtasks.put(subtask.getId(), subtask);
            final Epic epic = epics.get(subtask.getMyEpicId());
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic.getId());
        } else {
            System.out.println("Сначала необходимо создать эпик.");
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Нельзя обновить несуществующую задачу.");
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Нельзя обновить несуществующий эпик.");
        }
    }

    public void updateSubtask(Subtask subtask) {
        final int subtaskId = subtask.getId();

        if (subtasks.containsKey(subtaskId)) {
            subtasks.put(subtaskId, subtask);
            final Epic epic = getEpicById(subtask.getMyEpicId());
            updateEpicStatus(epic.getId());
        } else {
            System.out.println("Нельзя обновить несуществующую подзадачу.");
        }
    }

    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задача с ID " + id + " не найдена.");
        }
    }

    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            final Epic epic = getEpicById(id);
            final ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
            for (Integer subtasksId : subtasksIds) {
                subtasks.remove(subtasksId);
            }
            epics.remove(id);
        } else {
            System.out.println("Эпик с ID " + id + " не найден.");
        }
    }

    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            final Subtask subtask = getSubtaskById(id);
            final int epicId = subtask.getMyEpicId();
            final Epic epic = getEpicById(epicId);
            epic.removeSubtaskById(id);
            subtasks.remove(id);
            updateEpicStatus(epicId);
        } else {
            System.out.println("Подзадача с ID " + id + " не найдена.");
        }
    }

    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        final Epic epic = getEpicById(id);
        final ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
        final ArrayList<Subtask> subtasksByEpicId = new ArrayList<>();

        for (Integer subtaskId : subtasksIds) {
            subtasksByEpicId.add(subtasks.get(subtaskId));
        }
        return subtasksByEpicId;
    }

    public void updateEpicStatus(int epicId) {
        final Epic epic = epics.get(epicId);
        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : epic.getSubtasksIds()) {
            final Subtask subtask = subtasks.get(subtaskId);

            if (subtask == null) continue;
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (epic.getSubtasksIds().isEmpty() || allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}