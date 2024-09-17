package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler {
    protected TaskManager taskManager;
    protected Gson gson;
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange exchange, String text, int code) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, response.length);
        exchange.getResponseBody().write(response);
    }

    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, text, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, text, 406);
    }

    protected void sendIncorrectMethod(HttpExchange exchange, String method) throws IOException {
        String responseString = "Метод " + method + " не существует или некорректен.";
        sendResponse(exchange, responseString, 405);
    }

    protected void sendIncorrectPath(HttpExchange exchange, String path) throws IOException {
        String responseString = "Путь " + path + " некорректен.";
        sendResponse(exchange, responseString, 400);
    }

    protected void sendIncorrectId(HttpExchange exchange) throws IOException {
        String responseString = "Введён некорректный ID";
        sendResponse(exchange, responseString, 400);
    }

    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        String responseString = "Произошла ошибка при обработке запроса: " + message;
        sendResponse(exchange, responseString, 500);
    }

    protected void sendResponse(HttpExchange exchange, String text, int code) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, response.length);
        exchange.getResponseBody().write(response);
    }

    public Optional<Integer> getId(String path) {
        try {
            String[] splitPath = path.split("/");
            int id = Integer.parseInt(splitPath[2]);
            if (id <= 0) {
                return Optional.empty();
            }
            return Optional.of(id);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }
}