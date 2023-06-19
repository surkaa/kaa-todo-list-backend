package cn.surkaa.mapper;

import cn.surkaa.entity.UserAvatar;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author SurKaa
* @description 针对表【user_avatar(头像表)】的数据库操作Mapper
* @createDate 2023-06-19 19:43:53
* @Entity cn.surkaa.entity.UserAvatar
*/
@Mapper
public interface UserAvatarMapper extends BaseMapper<UserAvatar> {

}




