package model_test;

import model.Task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    // Проверяем, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    public void shouldBeEqualWithEqualId() {
        Task task1 = new Task("name 1", "description 1");
        Task task2 = new Task("name 2", "description 2");

        task1.setId(1);
        task2.setId(1);

        Assertions.assertEquals(task1, task2, "Таски с одинаковыми ID не равны");
    }
}