package cn.surkaa.mapper;

import cn.surkaa.module.domain.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper mapper;

    @Test
    public void insertTest() {
        User user = new User();
        user.setUserAccount("2024");
        user.setUserPassword("");
        int insert = mapper.insert(user);
        System.out.println("insert = " + insert);
    }

    @Test
    public void selectTest() {
        IPage<User> page = new PageDTO<>(1, 5);
        page = mapper.selectPage(page, null);
        System.out.println(page.getRecords());
    }

    @Test
    public void insertWithAvatarTest() {
        User user = new User();
        user.setUserAccount("2025");
        user.setUserPassword("");
        user.setAvatarId(1L);
        int insert = mapper.insert(user);
        System.out.println("insert = " + insert);
    }

}
