package cn.surkaa.service;

import cn.surkaa.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    protected UserService service;

    @Test
    public void doRegisterTest() {
        long res;

        // 账户密码以及确认密码都不为空(不是null 不是空字符)
        res = service.userRegister("", null, "1");
        assert res == -1;
        // 账户长度不小于6位
        res = service.userRegister("sas", null, "1");
        assert res == -1;
        // 密码不小于8位
        res = service.userRegister("a23456", "1", "1");
        assert res == -1;
        // 账户不能以数字开头
        res = service.userRegister("123456", "12345678", "12345678");
        assert res == -1;
        // 密码和校验密码相同
        res = service.userRegister("a23456", "12345678", "123456789");
        assert res == -1;
        // 账户和密码只能包含如下字符 a-z A-Z 0-9
        res = service.userRegister(".23456", "12345678", "12345678");
        assert res == -1;
        // 账户不重复
        service.userRegister("a23456", "12345678", "12345678");
        res = service.userRegister("a23456", "12345678", "12345678");
        assert res == -1;
        res = service.userRegister("b23456", "12345678", "12345678");
        assert res != -1;
    }

    @Test
    public void doLoginTest() {
        String account = "a23456";
        String password = "12345678";
        User user = service.doLogin(account, password, null);
        System.out.println("user = " + user);
        Assertions.assertEquals(user.getUserAccount(), account);
    }

}
