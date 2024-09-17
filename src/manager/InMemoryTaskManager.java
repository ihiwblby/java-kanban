package manager;

import exceptions.NotFoundException;
import exceptions.OverlappingException;
import model.Epic;
import model.Subtask;
import model.Task;
import utility.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected int idCounter = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(taskComparator);

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
        tasks.values().forEach(task -> {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        });
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.values().forEach(epic -> historyManager.remove(epic.getId()));
        epics.clear();
        subtasks.values().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        });
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        });
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.removeAllSubtasks();
            updateEpicStatus(epic.getId());
        });
    }

    @Override
    public Task getTaskById(int id) {
        final Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с ID " + id + " не найдена.");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        final Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с ID " + id + " не найден.");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с ID " + id + " не найдена.");
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        if (isTaskExists(task.getName(), task.getDescription())) {
            System.out.println("Задача с таким названием и описанием уже существует.");
            return;
        }
        checkTaskOverlapping(task);
        idCounter++;
        task.setId(idCounter);
        tasks.put(idCounter, task);
        addTaskToPrioritized(task);
    }

    @Override
    public void createEpic(Epic epic) {
        if (isTaskExists(epic.getName(), epic.getDescription())) {
            System.out.println("Эпик с таким названием и описанием уже существует.");
            return;
        }
        idCounter++;
        epic.setId(idCounter);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (isTaskExists(subtask.getName(), subtask.getDescription())) {
            System.out.println("Подзадача с таким названием и описанием уже существует.");
            return;
        }
        checkTaskOverlapping(subtask);
        if (!epics.containsKey(subtask.getMyEpicId())) {
            throw new NotFoundException("Сначала необходимо создать эпик. Эпик с ID "
                    + subtask.getMyEpicId() + " не найден.");
        }
        idCounter++;
        subtask.setId(idCounter);
        subtasks.put(idCounter, subtask);
        final Epic epic = epics.get(subtask.getMyEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic.getId());
        addTaskToPrioritized(subtask);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            checkTaskOverlapping(task);
            prioritizedTasks.remove(tasks.get(task.getId()));
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        } else {
            throw new NotFoundException("Задача с ID " + task.getId() + " не найдена.");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else {
            throw new NotFoundException("Эпик с ID " + epic.getId() + " не найден.");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final int subtaskId = subtask.getId();

        if (subtasks.containsKey(subtaskId)) {
            checkTaskOverlapping(subtask);
            prioritizedTasks.remove(subtasks.get(subtaskId));
            subtasks.put(subtaskId, subtask);
            final Epic epic = getEpicById(subtask.getMyEpicId());
            updateEpicStatus(epic.getId());
            prioritizedTasks.add(subtask);
        } else {
            throw new NotFoundException("Подзадача с ID " + subtaskId + " не найдена.");
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            throw new NotFoundException("Задача с ID " + id + " не найдена.");
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            final Epic epic = getEpicById(id);
            epic.getSubtasksIds().forEach(subtasksId -> {
                prioritizedTasks.remove(subtasks.get(subtasksId));
                subtasks.remove(subtasksId);
                historyManager.remove(subtasksId);
            });
            epics.remove(id);
            historyManager.remove(id);
        } else {
            throw new NotFoundException("Эпик с ID " + id + " не найден.");
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            prioritizedTasks.remove(subtasks.get(id));
            final Subtask subtask = getSubtaskById(id);
            final int epicId = subtask.getMyEpicId();
            final Epic epic = getEpicById(epicId);
            epic.removeSubtaskById(id);
            subtasks.remove(id);
            updateEpicStatus(epicId);
            historyManager.remove(id);
        } else {
            throw new NotFoundException("Подзадача с ID " + id + " не найдена.");
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        final Epic epic = getEpicById(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с ID " + id + " не найден.");
        }
        return epic.getSubtasksIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void updateEpicStatus(int epicId) {
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик с ID " + epicId + " не найден.");
        }

        boolean allDone = epic.getSubtasksIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .allMatch(subtask -> subtask.getStatus() == Status.DONE);

        boolean allNew = epic.getSubtasksIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .allMatch(subtask -> subtask.getStatus() == Status.NEW);

        if (epic.getSubtasksIds().isEmpty() || allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        recalculateEpicTimeAndDuration(epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public boolean isTaskExists(String name, String description) {
        return tasks.values().stream()
                .anyMatch(task -> task.getName().equals(name) && task.getDescription().equals(description))
                || epics.values().stream()
                .anyMatch(epic -> epic.getName().equals(name) && epic.getDescription().equals(description))
                || subtasks.values().stream()
                .anyMatch(subtask -> subtask.getName().equals(name) && subtask.getDescription().equals(description));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addTaskToPrioritized(Task task) {
        if (task.getStartTime() != LocalDateTime.MIN) {
            prioritizedTasks.add(task);
        }
    }

    protected void recalculateEpicTimeAndDuration(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtasksIds();

        if (subtaskIds.isEmpty()) {
            epic.setDuration(Duration.ofMinutes(0));
            epic.setStartTime(LocalDateTime.MIN);
            epic.setEndTime(LocalDateTime.MIN);
            return;
        }

        LocalDateTime startTime = subtaskIds.stream()
                .map(subtasks::get)
                .map(Task::getStartTime)
                .filter(time -> !time.equals(LocalDateTime.MIN))
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);

        LocalDateTime endTime = subtaskIds.stream()
                .map(subtasks::get)
                .map(Task::getEndTime)
                .filter(time -> !time.equals(LocalDateTime.MIN))
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);

        Duration duration = subtaskIds.stream()
                .map(subtasks::get)
                .map(Task::getDuration)
                .reduce(Duration.ofMinutes(0), Duration::plus);

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(duration);
    }

    private boolean areTasksOverlapping(Task task1, Task task2) {
        return !task1.getStartTime().isAfter(task2.getEndTime())
                && !task2.getStartTime().isAfter(task1.getEndTime());
    }

    public void checkTaskOverlapping(Task task) {
        getPrioritizedTasks().stream()
                .filter(existingTask -> existingTask.getId() != task.getId() && areTasksOverlapping(task, existingTask))
                .findFirst()
                .ifPresent(existingTask -> {
                    throw new OverlappingException("Задача пересекается по времени с другой задачей с ID: "
                            + existingTask.getId());
                });
    }
}