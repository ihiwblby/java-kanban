package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InMemoryTaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(InMemoryTaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if (method.equals("GET")) {
                if ("/history".equals(path)) {
                    handleGetHistory(exchange);
                } else {
                    sendIncorrectPath(exchange, path);
                }
            } else {
                sendIncorrectMethod(exchange, method);
            }
        } catch (Exception exp) {
            sendServerError(exchange);
        } finally {
            exchange.close();
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        try {
            if (taskManager.getHistory().isEmpty()) {
                sendNotFound(exchange, "История пустая.");
            } else {
                sendText(exchange, gson.toJson(taskManager.getHistory()), 200);
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }
 }
