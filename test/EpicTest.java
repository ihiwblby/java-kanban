import model.Epic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {

    // Проверяем, что наследники класса Task равны друг другу, если равен их id;
    @Test
    public void shouldBeEqualWithEqualId() {
        Epic epic1 = new Epic("name 1", "description 1");
        Epic epic2 = new Epic("name 2", "description 2");

        epic1.setId(1);
        epic2.setId(1);

        Assertions.assertEquals(epic1, epic2, "Эпики с одинаковыми ID не равны");
    }
}