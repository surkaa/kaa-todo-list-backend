package cn.surkaa.mapper;

import cn.surkaa.module.Todo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author SurKaa
* @description 针对表【todolist】的数据库操作Mapper
* @createDate 2023-07-18 15:13:16
* @Entity cn.surkaa.module.Todo
*/
@Mapper
public interface TodoMapper extends BaseMapper<Todo> {

}




