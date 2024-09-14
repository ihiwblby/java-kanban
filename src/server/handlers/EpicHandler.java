package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InMemoryTaskManager;
import model.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(InMemoryTaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {

                case "GET": {
                    if ("/epics".equals(path)) {
                        handleGetEpics(exchange);
                    } else if (path.matches("/epics/\\d+$")) {
                        Optional<Integer> id = getId(path);
                        if (id.isPresent()) {
                            handleGetEpicById(exchange, id.get());
                        } else {
                            sendIncorrectId(exchange);
                        }
                    } else if (path.matches("/epics/\\d+/subtasks")) {
                        Optional<Integer> id = getId(path);
                        if (id.isPresent()) {
                            handleGetEpicSubtasks(exchange, id.get());
                        } else {
                            sendIncorrectId(exchange);
                        }
                    } else {
                        sendIncorrectPath(exchange, path);
                    }
                    break;
                }

                case "POST": {
                    if ("/epics".equals(path)) {
                        handleCreateEpic(exchange);
                    } else if (path.startsWith("/epics")) {
                        Optional<Integer> id = getId(path);
                        if (id.isPresent()) {
                            handleUpdateEpic(exchange, id.get());
                        } else {
                            sendIncorrectId(exchange);
                        }
                    } else {
                        sendIncorrectPath(exchange, path);
                    }
                    break;
                }

                case "DELETE": {
                    if (path.startsWith("/epics")) {
                        Optional<Integer> id = getId(path);
                        if (id.isPresent()) {
                            handleDeleteEpic(exchange, id.get());
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

    private void handleDeleteEpic(HttpExchange exchange, int id) throws IOException {
        try {
            if (taskManager.getEpicById(id) == null) {
                sendNotFound(exchange, "Эпик с ID " + id + " не найден");
            } else {
                taskManager.removeEpicById(id);
                sendText(exchange, "Эпик с ID " + id + " удалён", 200);
            }
        } catch (Exception exp) {
            sendServerError(exchange);
        }
    }

    private void handleUpdateEpic(HttpExchange exchange, int id) throws IOException {
        if (taskManager.getEpicById(id) == null) {
            sendNotFound(exchange, "Эпик с ID " + id + " не найден");
        } else {
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                if (body.isEmpty()) {
                    sendNotFound(exchange, "Не передан эпик для обновления.");
                } else {
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (taskManager.isTaskOverlapping(epic)) {
                        sendHasInteractions(exchange);
                    } else {
                        taskManager.updateEpic(epic);
                        sendText(exchange, "Эпик успешно обновлён", 201);
                    }
                }
            } catch (Exception exp) {
                sendServerError(exchange);
            }
        }
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            if (body.isEmpty()) {
                sendNotFound(exchange, "Не передан эпик для добавления.");
            } else {
                Epic epic = gson.fromJson(body, Epic.class);
                if (taskManager.isTaskOverlapping(epic)) {
                    sendHasInteractions(exchange);
                } else {
                    taskManager.createEpic(epic);
                    sendText(exchange, "Эпик успешно добавлен", 201);
                }
            }
        } catch (Exception exp) {
            sendServerError(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        try {
            if (taskManager.getAllEpics().isEmpty()) {
                sendNotFound(exchange, "Список эпиков пуст.");
            } else {
                sendText(exchange, gson.toJson(taskManager.getAllEpics()), 200);
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }

    private void handleGetEpicById(HttpExchange exchange, int id) throws IOException {
        try {
            Epic epic = taskManager.getEpicById(id);
            if (epic == null) {
                sendNotFound(exchange, "Эпик с ID " + id + " не найден");
            } else {
                sendText(exchange, gson.toJson(epic), 200);
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, int id) throws IOException {
        try {
            Epic epic = taskManager.getEpicById(id);
            if (epic == null) {
                sendNotFound(exchange, "Эпик с ID " + id + " не найден");
            } else {
                sendText(exchange, gson.toJson(taskManager.getSubtasksByEpicId(id)), 200);
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }
}