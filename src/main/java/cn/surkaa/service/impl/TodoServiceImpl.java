package cn.surkaa.service.impl;

import cn.surkaa.mapper.TodoMapper;
import cn.surkaa.module.Todo;
import cn.surkaa.service.TodoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author SurKaa
 * @description 针对表【todolist】的数据库操作Service实现
 * @createDate 2023-07-18 15:13:16
 */
@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo>
        implements TodoService {

    @Override
    public List<Todo> getAllTodoByToken(Long userId) {
        var lqw = new LambdaQueryWrapper<Todo>();
        lqw.eq(Todo::getUid, userId);
        return getBaseMapper().selectList(lqw);
    }
}




