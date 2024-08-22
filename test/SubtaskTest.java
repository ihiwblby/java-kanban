import model.Subtask;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubtaskTest {

    // Проверяем, что наследники класса Task равны друг другу, если равен их id;
    @Test
    public void shouldBeEqualWithEqualId() {
        Subtask subtask1 = new Subtask("name 1", "description 1", 1);
        Subtask subtask2 = new Subtask("name 2", "description 2", 1);

        subtask1.setId(1);
        subtask2.setId(1);

        Assertions.assertEquals(subtask1, subtask2, "Таски с одинаковыми ID не равны");
    }
}