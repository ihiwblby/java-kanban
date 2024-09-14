package server_test;

import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
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

public class SubtaskHandlerTest extends HttpServerTest {

    @BeforeEach
    public void setUp() throws IOException {
        super.setUp();
    }

    @AfterEach
    public void shutDown() {
        super.shutDown();
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.createSubtask(subtask);

        HttpRequest request = createRequest("/subtasks", "GET", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasksList = gson.fromJson(response.body(), new SubtaskTypeToken().getType());

        assertEquals(200, response.statusCode(), "Ожидали иной код ответа");
        assertEquals(1, subtasksList.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        HttpRequest request = createRequest("/subtasks/" + subtaskId, "GET", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask requestedSubtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals(subtask.getName(), requestedSubtask.getName());
        assertEquals(subtask.getDescription(), requestedSubtask.getDescription());
    }

    @Test
    public void testCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        String jsonBody = gson.toJson(subtask);

        HttpRequest request = createRequest("/subtasks", "POST", jsonBody);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("Подзадача успешно добавлена"));
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        subtask.setName("Updated Subtask");
        subtask.setStartTime(LocalDateTime.now().plusDays(1));
        String jsonBody = gson.toJson(subtask);

        HttpRequest request = createRequest("/subtasks/" + subtaskId, "POST", jsonBody);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("Подзадача успешно обновлена"));
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        HttpRequest request = createRequest("/subtasks/" + subtaskId, "DELETE", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Подзадача с ID 2 удалена"));

        HttpRequest getRequest = createRequest("/subtasks/1", "GET", null);
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode());
    }

    private static class SubtaskTypeToken extends TypeToken<List<Subtask>> {

    }
}