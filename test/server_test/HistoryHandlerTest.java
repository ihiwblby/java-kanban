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

public class HistoryHandlerTest extends HttpServerTest {

    @BeforeEach
    public void setUp() throws IOException {
        super.setUp();
    }

    @AfterEach
    public void shutDown() {
        super.shutDown();
    }

    @Test
    public void testGetHistoryEmpty() throws IOException, InterruptedException {
        HttpRequest request = createRequest("/history", "GET", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидали код 404 для пустой истории");
        assertTrue(response.body().contains("История пустая"), "Ожидали сообщение о пустой истории");
    }

    @Test
    public void testGetHistoryWithTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(20), LocalDateTime.now().plusMinutes(15));
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        HttpRequest request = createRequest("/history", "GET", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> history = gson.fromJson(response.body(), new TaskTypeToken().getType());

        assertEquals(200, response.statusCode(), "Ожидали код 200 для истории");
        assertEquals(2, history.size(), "Некорректное количество задач в истории");
    }

    private static class TaskTypeToken extends TypeToken<List<Task>> {

    }
}