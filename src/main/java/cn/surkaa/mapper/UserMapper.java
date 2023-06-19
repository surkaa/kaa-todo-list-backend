package cn.surkaa.mapper;

import cn.surkaa.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author SurKaa
 * @description 针对表【user(用户)】的数据库操作Mapper
 * @createDate 2023-06-19 19:46:45
 * @Entity cn.surkaa.entity.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




