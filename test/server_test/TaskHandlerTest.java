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

public class TaskHandlerTest extends HttpServerTest {

    @BeforeEach
    public void setUp() throws IOException {
        super.setUp();
    }

    @AfterEach
    public void shutDown() {
        super.shutDown();
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.createTask(task);

        HttpRequest request = createRequest("/tasks", "GET", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksList = gson.fromJson(response.body(), new TaskTypeToken().getType());

        assertEquals(200, response.statusCode(), "Ожидали иной код ответа");
        assertEquals(1, tasksList.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.createTask(task);

        HttpRequest request = createRequest("/tasks/1", "GET", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task requestedTask = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(task.getName(), requestedTask.getName());
        assertEquals(task.getDescription(), requestedTask.getDescription());
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.now());
        String jsonBody = gson.toJson(task);

        HttpRequest request = createRequest("/tasks", "POST", jsonBody);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("Задача успешно добавлена"));
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        taskManager.createTask(task);
        int taskId = task.getId();

        task.setName("Updated task");
        task.setStartTime(LocalDateTime.now().plusDays(1));
        String jsonBody = gson.toJson(task);

        HttpRequest request = createRequest("/tasks/" + taskId, "POST", jsonBody);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("Задача успешно обновлена"));
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.createTask(task);

        HttpRequest request = createRequest("/tasks/1", "DELETE", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Задача с ID 1 удалена"));

        HttpRequest getRequest = createRequest("/tasks/1", "GET", null);
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode());
    }

    private static class TaskTypeToken extends TypeToken<List<Task>> {

    }
}