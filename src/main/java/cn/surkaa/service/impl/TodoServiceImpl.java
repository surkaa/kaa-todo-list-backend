package cn.surkaa.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.surkaa.exception.UserCenterException;
import cn.surkaa.exception.error.ErrorEnum;
import cn.surkaa.mapper.TodoMapper;
import cn.surkaa.module.domain.Todo;
import cn.surkaa.module.request.todo.*;
import cn.surkaa.service.TodoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Todo> getAllTodoByToken(Long userId) {
        var lqw = new LambdaQueryWrapper<Todo>();
        lqw.eq(Todo::getUid, userId);
        return getBaseMapper().selectList(lqw);
    }

    @Override
    public Long saveTodoWithToken(Long userId, TodoSaveBody todoSaveBody) {
        if (todoSaveBody.getTitle() == null) {
            throw new UserCenterException(
                    ErrorEnum.INSERT_TODO_ERROR, "请提供笔记标题"
            );
        }
        Todo todo = new Todo();
        todo.setUid(userId);
        todo.setTitle(todoSaveBody.getTitle());
        String description = todoSaveBody.getDescription();
        if (description != null) {
            todo.setDescription(description);
        }
        Long dateUnix = todoSaveBody.getTargetTime();
        if (dateUnix != null && dateUnix != 0L) {
            if (dateUnix <= new Date().getTime()) {
                log.debug("传入的时间不对劲!");
                throw new UserCenterException(
                        ErrorEnum.INSERT_TODO_ERROR,
                        "预计完成时间过近或者已经过了预计时间"
                );
            }
            Date date = new Date(dateUnix);
            log.debug("解析时间: {}", date);
            todo.setTargetTime(date);
        }
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
    public boolean modifyFlagTodo(Long userId, TodoFlagRequest todoFlag) {
        log.debug("开始标记todo");
        Todo todo = new Todo();
        todo.setId(todoFlag.getId());
        todo.setUid(userId);
        todo.setFlag(todoFlag.getFlag() ? 1 : 0);
        log.debug("标记成功: {}, 开始更新", todo);
        return updateOneField(todo);
    }

    @Override
    public boolean modifyTitleTodo(Long userId, TodoTitleRequest todoTitle) {
        log.debug("开始修改todo");
        Todo todo = new Todo();
        todo.setId(todoTitle.getId());
        todo.setUid(userId);
        todo.setTitle(todoTitle.getTitle());
        log.debug("修改成功: {}, 开始更新", todo);
        return updateOneField(todo);
    }

    @Override
    public boolean modifyDescTodo(Long userId, TodoDescRequest todoDesc) {
        log.debug("开始修改todo desc");
        Todo todo = new Todo();
        todo.setId(todoDesc.getId());
        todo.setUid(userId);
        todo.setDescription(todoDesc.getDescription());
        log.debug("修改成功: {}, 开始更新", todo);
        return updateOneField(todo);
    }

    @Override
    public boolean modifyTargetTodo(Long userId, TodoTargetRequest todoTarget) {
        log.debug("开始修改todo desc");
        Todo todo = new Todo();
        todo.setId(todoTarget.getId());
        todo.setUid(userId);
        String target = todoTarget.getTarget();
        try {
            Date parse = SDF.parse(target);
            todo.setTargetTime(parse);
            log.debug("修改成功: {}, 开始更新", todo);
            return updateOneField(todo);
        } catch (ParseException e) {
            throw new UserCenterException(
                    ErrorEnum.UPDATE_TODO_ERROR,
                    "无法解析目标时间(" + target + "), 请使用时间格式: yyyy-MM-dd HH:mm:ss"
            );
        }
    }

    private boolean updateOneField(Todo todo) {
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




