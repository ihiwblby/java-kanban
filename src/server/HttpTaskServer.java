package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.InMemoryTaskManager;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import server.adapters.StatusAdapter;
import server.handlers.*;
import utility.Status;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    public static final int PORT = 8080;

    private final HttpServer httpServer;
    private final Gson gson;
    public InMemoryTaskManager taskManager;

    public HttpTaskServer(InMemoryTaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = getGson();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер на " + PORT + " порту остановлен.");
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .setPrettyPrinting();
        return gsonBuilder.create();
    }

    public static void main(String[] args) throws IOException {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }
}
