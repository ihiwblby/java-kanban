public class Subtask extends Task {
    private final int myEpicId;

    public Subtask(String name, String description, int myEpicId) {
        super(name, description);
        this.myEpicId = myEpicId;
    }

    public int getMyEpicId() {
        return myEpicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", myEpicId=" + myEpicId +
                '}';
    }
}
