package server_test;

import com.google.gson.reflect.TypeToken;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrioritizedHandlerTest extends HttpServerTest {

    @BeforeEach
    public void setUp() throws IOException {
        super.setUp();
    }

    @AfterEach
    public void shutDown() {
        super.shutDown();
    }

    @Test
    public void testGetPrioritizedTasksEmpty() throws IOException, InterruptedException {
        HttpRequest request = createRequest("/prioritized", "GET", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидали код 404 для пустого списка приоритетных задач");
        assertTrue(response.body().contains("Список пуст."), "Ожидали сообщение о пустом списке приоритетных задач");
    }

    @Test
    public void testGetPrioritizedTasksWithTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(20), LocalDateTime.now().plusMinutes(15));
        taskManager.createTask(task2);

        taskManager.getPrioritizedTasks();

        HttpRequest request = createRequest("/prioritized", "GET", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TaskTypeToken().getType());

        assertEquals(200, response.statusCode(), "Ожидали код 200 для списка приоритетных задач");
        assertEquals(2, prioritizedTasks.size(), "Некорректное количество задач в списке приоритетных задач");
        assertEquals("Task 1", prioritizedTasks.getFirst().getName());
    }

    private static class TaskTypeToken extends TypeToken<List<Task>> {

    }
}