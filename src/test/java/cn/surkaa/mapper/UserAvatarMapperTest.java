package cn.surkaa.mapper;

import cn.surkaa.module.UserAvatar;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserAvatarMapperTest {

    @Autowired
    private UserAvatarMapper mapper;

    @Test
    public void insertTest() {
        UserAvatar avatar = new UserAvatar();
        avatar.setAvatar(new byte[]{12, 117, -120});
        int insert = mapper.insert(avatar);
        System.out.println("insert = " + insert);
    }

    @Test
    public void selectTest() {
        PageDTO<UserAvatar> page = new PageDTO<>(1, 5);
        page = mapper.selectPage(page, null);
        System.out.println(page.getRecords());
    }

}
