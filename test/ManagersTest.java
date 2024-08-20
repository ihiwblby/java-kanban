import manager.Managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    // Проверяем, что утилитарный класс всегда возвращает
    // проинициализированный экземпляр TaskManager;
    @Test
    public void shouldReturnInitializedTaskManager() {
        Assertions.assertNotNull(Managers.getDefault());
    }

    // Проверяем, что утилитарный класс всегда возвращает
    // проинициализированный экземпляр HistoryManager;
    @Test
    void shouldReturnInitializedHistoryManager() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}