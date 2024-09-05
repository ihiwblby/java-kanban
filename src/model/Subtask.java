package model;

import utility.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int myEpicId;

    public Subtask(String name, String description, int myEpicId) {
        super(name, description);
        this.myEpicId = myEpicId;
    }

    public Subtask(String name, String description, int myEpicId, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.myEpicId = myEpicId;
    }

    public int getMyEpicId() {
        return myEpicId;
    }

    public void setMyEpicId(int myEpicId) {
        if (getId() == myEpicId) {
            System.out.println("Сабтаск нельзя добавить в самого себя");
        } else {
            this.myEpicId = myEpicId;
        }
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public Subtask clone() {
        return (Subtask) super.clone();
    }

    @Override
    public String toString() {
        return "model.Subtask{" +
                "type=" + getType() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id + '\'' +
                ", status=" + status + '\'' +
                ", myEpicId=" + myEpicId + '\'' +
                ", duration=" + duration + '\'' +
                ", startTime=" + startTime + '\'' +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public String toStringForFile() {
        String[] string = {
                Integer.toString(getId()),
                getType().toString(),
                getName(),
                getStatus().toString(),
                getDescription(),
                Integer.toString(getMyEpicId()),
                Long.toString(getDuration().toMinutes()),
                getStartTime().toString()
        };
        return String.join(",", string);
    }
}
