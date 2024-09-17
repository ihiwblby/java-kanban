package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Subtask;
import exceptions.NotFoundException;
import exceptions.OverlappingException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {

                case "GET": {
                    if ("/subtasks".equals(path)) {
                        handleGetSubtasks(exchange);
                    } else if (path.startsWith("/subtasks")) {
                        Optional<Integer> id = getId(path);
                        if (id.isPresent()) {
                            handleGetSubtaskById(exchange, id.get());
                        } else {
                            sendIncorrectId(exchange);
                        }
                    } else {
                        sendIncorrectPath(exchange, path);
                    }
                    break;
                }

                case "POST": {
                    if ("/subtasks".equals(path)) {
                        handleCreateSubtask(exchange);
                    } else if (path.startsWith("/subtasks")) {
                        Optional<Integer> id = getId(path);
                        if (id.isPresent()) {
                            handleUpdateSubtask(exchange);
                        } else {
                            sendIncorrectId(exchange);
                        }
                    } else {
                        sendIncorrectPath(exchange, path);
                    }
                    break;
                }

                case "DELETE": {
                    if (path.startsWith("/subtasks")) {
                        Optional<Integer> id = getId(path);
                        if (id.isPresent()) {
                            handleDeleteSubtask(exchange, id.get());
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

    private void handleDeleteSubtask(HttpExchange exchange, int id) throws IOException {
        try {
            taskManager.removeSubtaskById(id);
            sendText(exchange, "Подзадача с ID " + id + " удалена", 200);
        } catch (NotFoundException exp) {
            sendNotFound(exchange, exp.getMessage());
        } catch (Exception exp) {
            sendServerError(exchange, exp.getMessage());
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            if (body.isEmpty()) {
                sendNotFound(exchange, "Не передана подзадача для обновления.");
                return;
            }
            Subtask subtask = gson.fromJson(body, Subtask.class);
            taskManager.updateSubtask(subtask);
            sendText(exchange, "Подзадача успешно обновлена", 201);
        } catch (NotFoundException exp) {
            sendNotFound(exchange, exp.getMessage());
        } catch (OverlappingException exp) {
            sendHasInteractions(exchange, exp.getMessage());
        } catch (Exception exp) {
            sendServerError(exchange, exp.getMessage());
        }
    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            if (body.isEmpty()) {
                sendNotFound(exchange, "Не передана подзадача для добавления.");
                return;
            }
            Subtask subtask = gson.fromJson(body, Subtask.class);
            taskManager.createSubtask(subtask);
            sendText(exchange, "Подзадача успешно добавлена", 201);
        } catch (OverlappingException exp) {
            sendHasInteractions(exchange, exp.getMessage());
        } catch (Exception exp) {
            sendServerError(exchange, exp.getMessage());
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        try {
            if (taskManager.getAllSubtasks().isEmpty()) {
                sendNotFound(exchange, "Список подзадач пуст.");
            } else {
                sendText(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
            }
        } catch (Exception exp) {
            sendServerError(exchange, exp.getMessage());
        }
    }

    private void handleGetSubtaskById(HttpExchange exchange, int id) throws IOException {
        try {
            Subtask subtask = taskManager.getSubtaskById(id);
            sendText(exchange, gson.toJson(subtask), 200);
        } catch (NotFoundException exp) {
            sendNotFound(exchange, exp.getMessage());
        } catch (Exception exp) {
            sendServerError(exchange, exp.getMessage());
        }
    }
}