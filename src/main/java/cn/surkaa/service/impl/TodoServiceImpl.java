package cn.surkaa.service.impl;

import cn.surkaa.exception.UserCenterException;
import cn.surkaa.exception.error.ErrorEnum;
import cn.surkaa.mapper.TodoMapper;
import cn.surkaa.module.domain.Todo;
import cn.surkaa.module.request.TodoFlagRequest;
import cn.surkaa.service.TodoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author SurKaa
 * @description 针对表【todolist】的数据库操作Service实现
 * @createDate 2023-07-18 15:13:16
 */
@Service
@Slf4j
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo>
        implements TodoService {

    @Override
    public List<Todo> getAllTodoByToken(Long userId) {
        var lqw = new LambdaQueryWrapper<Todo>();
        lqw.eq(Todo::getUid, userId);
        return getBaseMapper().selectList(lqw);
    }

    @Override
    public Long saveTodoWithToken(Long userId, Todo todo) {
        if (todo.getId() != null) {
            throw new UserCenterException(
                    ErrorEnum.INSERT_TODO_ERROR, "请不要带上id"
            );
        }
        if (todo.getTitle() == null) {
            throw new UserCenterException(
                    ErrorEnum.INSERT_TODO_ERROR, "请提供笔记标题"
            );
        }
        Long uid = todo.getUid();
        if (uid == null) {
            todo.setUid(userId);
        } else if (!uid.equals(userId)) {
            throw new UserCenterException(
                    ErrorEnum.INSERT_TODO_ERROR, "您无法为其他人保存待办事项");
        }
        log.debug("开始保存Todo");
        boolean flag = this.save(todo);
        if (flag) {
            log.debug("保存成功");
            return todo.getId();
        } else {
            log.debug("保存失败");
            throw new UserCenterException(ErrorEnum.INSERT_TODO_ERROR);
        }
    }

    @Override
    public boolean flagTodo(Long userId, TodoFlagRequest todoFlag) {
        log.debug("开始标记todo");
        Todo todo = new Todo();
        todo.setId(todoFlag.getId());
        todo.setUid(userId);
        todo.setFlag(todoFlag.getFlag() ? 1 : 0);
        log.debug("标记成功: {}, 开始更新", todo);
        boolean res = this.updateById(todo);
        if (res) {
            log.debug("更新成功");
            return true;
        } else {
            log.debug("更新失败");
            throw new UserCenterException(
                    ErrorEnum.UPDATE_TODO_ERROR
            );
        }
    }
}




