package server_test;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.Managers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import server.HttpTaskServer;

import java.io.IOException;

public class HttpServerTest {
    protected InMemoryTaskManager taskManager;
    protected HttpTaskServer taskServer;
    protected Gson gson;
    protected HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    protected static HttpRequest createRequest(String path, String method, String jsonBody) {
        URI uri = URI.create("http://localhost:8080" + path);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri);

        HttpRequest.BodyPublisher bodyPublisher = jsonBody != null
                ? HttpRequest.BodyPublishers.ofString(jsonBody)
                : HttpRequest.BodyPublishers.noBody();

        return switch (method.toUpperCase()) {
            case "GET" -> requestBuilder.GET().build();
            case "POST" -> requestBuilder.POST(bodyPublisher).build();
            case "DELETE" -> requestBuilder.DELETE().build();
            default -> throw new IllegalArgumentException("Некорректный метод: " + method);
        };
    }
}