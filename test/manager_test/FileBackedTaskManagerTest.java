package manager_test;

import manager.FileBackedTaskManager;
import manager.ManagerSaveException;
import model.Epic;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{

    private File tempFile;
    private FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("task_manager_test", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void afterEach() {
        tempFile.delete();
    }

    @Test
    void shouldHandleEmptyFileCorrectly() {
        List<Task> tasks = fileBackedTaskManager.getAllTasks();
        Assertions.assertTrue(tasks.isEmpty(), "Список задач должен быть пустым после загрузки из пустого файла.");
    }

    @Test
    void shouldSaveAndLoadMultipleTasksCorrectly() {
        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.now());
        fileBackedTaskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(20));
        fileBackedTaskManager.createTask(task2);

        fileBackedTaskManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> loadedTasks = loadedManager.getAllTasks();

        Assertions.assertEquals(2, loadedTasks.size(), "Должно быть загружено две задачи.");
        Assertions.assertEquals(task1, loadedTasks.get(0), "Первая задача должна совпадать с сохраненной.");
        Assertions.assertEquals(task2, loadedTasks.get(1), "Вторая задача должна совпадать с сохраненной.");
    }

    @Test
    void shouldThrowExceptionWhenFileIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            new FileBackedTaskManager(null));
    }

    @Test
    void shouldThrowExceptionOnFileSaveError() {
        File invalidFile = new File("/invalid_path/task_manager_test.csv");
        FileBackedTaskManager invalidManager = new FileBackedTaskManager(invalidFile);
        Task task = new Task("Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.now());
        Assertions.assertThrows(ManagerSaveException.class, () -> invalidManager.createTask(task));
    }

    @Test
    void shouldThrowExceptionOnWrongData() throws IOException {
        File tempFile = File.createTempFile("corrupt_data_test", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("invalid,data,format\n");
            writer.write("vytytyhfdbnczgbcfnmntcns\n");
        }
        Assertions.assertThrows(IllegalArgumentException.class, () -> FileBackedTaskManager.loadFromFile(tempFile));
    }

    @Test
    void shouldThrowExceptionOnNullOrEmptyString() {
        FileBackedTaskManager manager = new FileBackedTaskManager(new File("test.csv"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> manager.fromString(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> manager.fromString(""));
    }
}