package manager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
