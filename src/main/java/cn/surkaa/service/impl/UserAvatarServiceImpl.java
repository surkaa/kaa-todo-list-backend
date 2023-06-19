package cn.surkaa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.surkaa.entity.UserAvatar;
import cn.surkaa.service.UserAvatarService;
import cn.surkaa.mapper.UserAvatarMapper;
import org.springframework.stereotype.Service;

/**
* @author SurKaa
* @description 针对表【user_avatar(头像表)】的数据库操作Service实现
* @createDate 2023-06-19 19:43:53
*/
@Service
public class UserAvatarServiceImpl extends ServiceImpl<UserAvatarMapper, UserAvatar>
    implements UserAvatarService{

}




