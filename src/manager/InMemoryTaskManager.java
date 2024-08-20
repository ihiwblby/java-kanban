package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import utility.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idCounter = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public Task getTaskById(int id) {
        final Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        final Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    @Override
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

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Нельзя обновить несуществующую задачу.");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Нельзя обновить несуществующий эпик.");
        }
    }

    @Override
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

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задача с ID " + id + " не найдена.");
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            final Epic epic = getEpicById(id);
            final ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
            for (Integer subtasksId : subtasksIds) {
                subtasks.remove(subtasksId);
                historyManager.remove(subtasksId);
            }
            epics.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Эпик с ID " + id + " не найден.");
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            final Subtask subtask = getSubtaskById(id);
            final int epicId = subtask.getMyEpicId();
            final Epic epic = getEpicById(epicId);
            epic.removeSubtaskById(id);
            subtasks.remove(id);
            updateEpicStatus(epicId);
            historyManager.remove(id);
        } else {
            System.out.println("Подзадача с ID " + id + " не найдена.");
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        final Epic epic = getEpicById(id);
        final ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
        final ArrayList<Subtask> subtasksByEpicId = new ArrayList<>();

        for (Integer subtaskId : subtasksIds) {
            subtasksByEpicId.add(subtasks.get(subtaskId));
        }
        return subtasksByEpicId;
    }

    @Override
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}