package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import exceptions.OverlappingException;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
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
                            handleUpdateTask(exchange);
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
            sendServerError(exchange, exp.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleDeleteTask(HttpExchange exchange, int taskId) throws IOException {
        try {
            taskManager.removeTaskById(taskId);
            sendText(exchange, "Задача с ID " + taskId + " удалена", 200);
        } catch (NotFoundException exp) {
            sendNotFound(exchange, exp.getMessage());
        } catch (Exception exp) {
            sendServerError(exchange, exp.getMessage());
        }
    }

    private void handleUpdateTask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            if (body.isEmpty()) {
                sendNotFound(exchange, "Не передана задача для обновления.");
                return;
            }
            Task task = gson.fromJson(body, Task.class);
            taskManager.updateTask(task);
            sendText(exchange, "Задача успешно обновлена", 201);
        } catch (NotFoundException exp) {
            sendNotFound(exchange, exp.getMessage());
        } catch (OverlappingException exp) {
            sendHasInteractions(exchange, exp.getMessage());
        } catch (Exception exp) {
            sendServerError(exchange, exp.getMessage());
        }
    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            if (body.isEmpty()) {
                sendNotFound(exchange, "Не передана задача для добавления.");
                return;
            }
            Task task = gson.fromJson(body, Task.class);
            taskManager.createTask(task);
            sendText(exchange, "Задача успешно добавлена", 201);
        } catch (OverlappingException exp) {
            sendHasInteractions(exchange, exp.getMessage());
        } catch (Exception exp) {
            sendServerError(exchange, exp.getMessage());
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        try {
            if (taskManager.getAllTasks().isEmpty()) {
                sendNotFound(exchange, "Список задач пуст.");
            } else {
                sendText(exchange, gson.toJson(taskManager.getAllTasks()), 200);
            }
        } catch (Exception exp) {
            sendServerError(exchange, exp.getMessage());
        }
    }

    private void handleGetTaskById(HttpExchange exchange, int id) throws IOException {
        try {
            Task task = taskManager.getTaskById(id);
            sendText(exchange, gson.toJson(task), 200);
        } catch (NotFoundException exp) {
            sendNotFound(exchange, exp.getMessage());
        } catch (Exception exp) {
            sendServerError(exchange, exp.getMessage());
        }
    }
}