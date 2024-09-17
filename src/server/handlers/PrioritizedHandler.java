package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if (method.equals("GET")) {
                if ("/prioritized".equals(path)) {
                    handleGetPrioritizedTasks(exchange);
                } else {
                    sendIncorrectPath(exchange, path);
                }
            } else {
                sendIncorrectMethod(exchange, method);
            }
        } catch (Exception exp) {
            sendServerError(exchange, exp.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        try {
            if (taskManager.getPrioritizedTasks().isEmpty()) {
                sendNotFound(exchange, "Список пуст.");
            } else {
                sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
            }
        } catch (Exception exp) {
            sendServerError(exchange, exp.getMessage());
        }
    }
}
