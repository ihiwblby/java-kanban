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

public class EpicHandlerTest extends HttpServerTest {

    @BeforeEach
    public void setUp() throws IOException {
        super.setUp();
    }

    @AfterEach
    public void shutDown() {
        super.shutDown();
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        HttpRequest request = createRequest("/epics", "GET", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicsList = gson.fromJson(response.body(), new EpicTypeToken().getType());

        assertEquals(200, response.statusCode(), "Ожидали иной код ответа");
        assertEquals(1, epicsList.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        HttpRequest request = createRequest("/epics/" + epicId, "GET", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic requestedEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(epic.getName(), requestedEpic.getName());
        assertEquals(epic.getDescription(), requestedEpic.getDescription());
    }

    @Test
    public void testCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic Description");
        String jsonBody = gson.toJson(epic);

        HttpRequest request = createRequest("/epics", "POST", jsonBody);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("Эпик успешно добавлен"));
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        epic.setName("Updated Epic");
        String jsonBody = gson.toJson(epic);

        HttpRequest request = createRequest("/epics/" + epicId, "POST", jsonBody);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("Эпик успешно обновлён"));
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        HttpRequest request = createRequest("/epics/1", "DELETE", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Эпик с ID 1 удалён"));

        HttpRequest getRequest = createRequest("/epics/1", "GET", null);
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode());
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.createSubtask(subtask);
        int epicId = epic.getId();

        HttpRequest request = createRequest("/epics/" + epicId + "/subtasks", "GET", null);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasksList = gson.fromJson(response.body(), new SubtaskTypeToken().getType());

        assertEquals(200, response.statusCode());
        assertEquals(1, subtasksList.size(), "Некорректное количество подзадач для эпика");
    }

    private static class EpicTypeToken extends TypeToken<List<Epic>> {
    }

    private static class SubtaskTypeToken extends TypeToken<List<Subtask>> {
    }
}