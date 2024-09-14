package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InMemoryTaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(InMemoryTaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {

                case "GET": {
                    if ("/tasks".equals(path)) {
                        handleGetTasks(exchange);
                    } else if (path.startsWith("/tasks")) {
                        Optional<Integer> id = getId(path);
                        if (id.isPresent()) {
                            handleGetTaskById(exchange, id.get());
                        } else {
                            sendIncorrectId(exchange);
                        }
                    } else {
                        sendIncorrectPath(exchange, path);
                    }
                    break;
                }

                case "POST": {
                    if ("/tasks".equals(path)) {
                        handleCreateTask(exchange);
                    } else if (path.startsWith("/tasks")) {
                        Optional<Integer> id = getId(path);
                        if (id.isPresent()) {
                            handleUpdateTask(exchange, id.get());
                        } else {
                            sendIncorrectId(exchange);
                        }
                    } else {
                        sendIncorrectPath(exchange, path);
                    }
                    break;
                }

                case "DELETE": {
                    if (path.startsWith("/tasks")) {
                        Optional<Integer> id = getId(path);
                        if (id.isPresent()) {
                            handleDeleteTask(exchange, id.get());
                        } else {
                            sendIncorrectId(exchange);
                        }
                    } else {
                        sendIncorrectPath(exchange, path);
                    }
                    break;
                }

                default: {
                    sendIncorrectMethod(exchange, method);
                    break;
                }
            }
        } catch (Exception exp) {
            sendServerError(exchange);
        } finally {
            exchange.close();
        }
    }

    private void handleDeleteTask(HttpExchange exchange, int taskId) throws IOException {
        try {
            if (taskManager.getTaskById(taskId) == null) {
                sendNotFound(exchange, "Задача с ID " + taskId + " не найдена");
            } else {
                taskManager.removeTaskById(taskId);
                sendText(exchange, "Задача с ID " + taskId + " удалена", 200);
            }
        } catch (Exception exp) {
            sendServerError(exchange);
        }
    }

    private void handleUpdateTask(HttpExchange exchange, int taskId) throws IOException {
        if (taskManager.getTaskById(taskId) == null) {
            sendNotFound(exchange, "Задача с ID " + taskId + " не найдена");
        } else {
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                if (body.isEmpty()) {
                    sendNotFound(exchange, "Не передана задача для обновления.");
                } else {
                    Task task = gson.fromJson(body, Task.class);
                    if (taskManager.isTaskOverlapping(task)) {
                        sendHasInteractions(exchange);
                    } else {
                        taskManager.updateTask(task);
                        sendText(exchange, "Задача успешно обновлена", 201);
                    }
                }
            } catch (Exception exp) {
                sendServerError(exchange);
            }
        }
    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            if (body.isEmpty()) {
                sendNotFound(exchange, "Не передана задача для добавления.");
            } else {
                Task task = gson.fromJson(body, Task.class);
                if (taskManager.isTaskOverlapping(task)) {
                    sendHasInteractions(exchange);
                } else {
                    taskManager.createTask(task);
                    sendText(exchange, "Задача успешно добавлена", 201);
                }
            }
        } catch (Exception exp) {
            sendServerError(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        try {
            if (taskManager.getAllTasks().isEmpty()) {
                sendNotFound(exchange, "Список задач пуст.");
            } else {
                sendText(exchange, gson.toJson(taskManager.getAllTasks()), 200);
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }

    private void handleGetTaskById(HttpExchange exchange, int id) throws IOException {
        try {
            Task task = taskManager.getTaskById(id);
            if (task == null) {
                sendNotFound(exchange, "Задача с ID " + id + " не найдена");
            } else {
                sendText(exchange, gson.toJson(task), 200);
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }
}