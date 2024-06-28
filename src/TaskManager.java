import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public ArrayList<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            return null;
        } else {
            final ArrayList<Task> allTasks = new ArrayList<>();
            for (Task task : tasks.values()) {
                allTasks.add(task);
            }
            return allTasks;
        }
    }

    public ArrayList<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            return null;
        } else {
            final ArrayList<Epic> allEpics = new ArrayList<>();
            for (Epic epic : epics.values()) {
                allEpics.add(epic);
            }
            return allEpics;
        }
    }

    public ArrayList<Subtask> getAllSubtasks() {
        if (subtasks.isEmpty()) {
            return null;
        } else {
            final ArrayList<Subtask> allSubtasks = new ArrayList<>();
            for (Subtask subtask : subtasks.values()) {
                allSubtasks.add(subtask);
            }
            return allSubtasks;
        }
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
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getMyEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            final Epic epic = epics.get(subtask.getMyEpicId());
            if (!epic.getSubtasksIds().contains(subtask.getId())) {
                epic.addSubtaskId(subtask.getId());
            }
            updateEpicStatus(epic.getId());
        } else {
            System.out.println("Сначала необходимо создать задачу.");
        }
    }

    private void updateTask(Task task, int id) {
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        } else {
            System.out.println("Нельзя обновить несуществующую задачу.");
        }
    }

    private void updateEpic(Epic epic, int id) {
        if (epics.containsKey(id)) {
            epics.put(id, epic);
        } else {
            System.out.println("Нельзя обновить несуществующую задачу.");
        }
    }

    private void updateSubtask(Subtask subtask, int id) {
        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);
            final Epic epic = getEpicById(subtask.getMyEpicId());
            epic.addSubtaskId(id);
            updateEpicStatus(epic.getId());
        } else {
            System.out.println("Нельзя обновить несуществующую подзадачу.");
        }
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        final Epic epic = getEpicById(id);
        final ArrayList<Integer> subtasksIds = epic.getSubtasksIds();

        for (Integer subtasksId : subtasksIds) {
            subtasks.remove(subtasksId);
        }

        epics.remove(id);
    }

    public void removeSubtaskById(int id) {
        final Subtask subtask = getSubtaskById(id);
        final int epicId = subtask.getMyEpicId();
        final Epic epic = getEpicById(epicId);
        epic.removeSubtaskById(id);
        subtasks.remove(id);
        updateEpicStatus(epicId);
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
            if (subtask == null) {
                allDone = false;
            }
             else if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            else if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}