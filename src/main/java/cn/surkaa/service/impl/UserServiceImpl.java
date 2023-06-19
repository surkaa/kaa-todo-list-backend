package cn.surkaa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.surkaa.entity.User;
import cn.surkaa.service.UserService;
import cn.surkaa.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author SurKaa
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-06-19 19:46:45
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




