package cn.surkaa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.surkaa.module.Todo;
import cn.surkaa.service.TodoService;
import cn.surkaa.mapper.TodoMapper;
import org.springframework.stereotype.Service;

/**
* @author SurKaa
* @description 针对表【todolist】的数据库操作Service实现
* @createDate 2023-07-18 15:13:16
*/
@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo>
    implements TodoService {

}




