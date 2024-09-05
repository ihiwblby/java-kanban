package model_test;

import manager.InMemoryTaskManager;
import model.Epic;
import model.Subtask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utility.Status;

import java.time.Duration;
import java.time.LocalDateTime;

class EpicTest {

    private InMemoryTaskManager manager;
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Epic", "Description");
        manager.createEpic(epic);

        subtask1 = new Subtask("Subtask 1", "Description", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        subtask2 = new Subtask("Subtask 2", "Description", epic.getId(), Duration.ofMinutes(45), LocalDateTime.now().plusMinutes(60));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
    }

    @Test
    public void shouldBeEqualWithEqualId() {
        Epic epic1 = new Epic("name 1", "description 1");
        Epic epic2 = new Epic("name 2", "description 2");

        epic1.setId(1);
        epic2.setId(1);

        Assertions.assertEquals(epic1, epic2, "Эпики с одинаковыми ID не равны");
    }

    @Test
    public void testAllSubtasksNew() {
        Assertions.assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW");
    }

    @Test
    public void testAllSubtasksDone() {
        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);

        manager.updateEpicStatus(epic.getId());
        Assertions.assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE");
    }

    @Test
    public void testMixedSubtasks() {
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);

        manager.updateEpicStatus(epic.getId());
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    public void testAllSubtasksInProgress() {
        subtask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask2);

        manager.updateEpicStatus(epic.getId());
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");
    }
}