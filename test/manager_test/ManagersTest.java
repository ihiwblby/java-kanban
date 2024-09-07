package manager_test;

import manager.Managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    public void shouldReturnInitializedTaskManager() {
        Assertions.assertNotNull(Managers.getDefault());
    }

    @Test
    void shouldReturnInitializedHistoryManager() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}