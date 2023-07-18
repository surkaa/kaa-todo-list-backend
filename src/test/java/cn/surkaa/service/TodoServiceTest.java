package cn.surkaa.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TodoServiceTest {

    @Autowired
    protected TodoService service;

    @Test
    public void testSelectListByUser() {
        System.out.println(service.getAllTodoByToken(10L));
    }

}
