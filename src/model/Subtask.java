package model;

public class Subtask extends Task {
    private int myEpicId;

    public Subtask(String name, String description, int myEpicId) {
        super(name, description);
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
    public Subtask clone() {
        return (Subtask) super.clone();
    }

    @Override
    public String toString() {
        return "model.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", myEpicId=" + myEpicId +
                '}';
    }
}
