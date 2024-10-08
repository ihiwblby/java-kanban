package model;

import utility.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubtaskId(int subtasksId) {
        if (getId() == subtasksId) {
            System.out.println("Эпик нельзя добавить в самого себя в качестве подзадачи.");
        } else {
            subtasksIds.add(subtasksId);
        }
    }

    public void removeSubtaskById(Integer subtaskId) {
        subtasksIds.remove(subtaskId);
    }

    public void removeAllSubtasks() {
        subtasksIds.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime != null ? endTime : LocalDateTime.MIN;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public Epic clone() {
        Epic cloned = (Epic) super.clone();
        cloned.subtasksIds = new ArrayList<>(this.subtasksIds);
        return cloned;
    }

    @Override
    public String toString() {
        return "model.Epic{" +
                ", type=" + getType() +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id + '\'' +
                ", status=" + status + '\'' +
                ", subtasksIds=" + subtasksIds + '\'' +
                ", duration=" + duration + '\'' +
                ", startTime=" + startTime + '\'' +
                ", endTime=" + getEndTime() +
                '}';
    }
}
