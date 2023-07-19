package cn.surkaa.controller;

import cn.surkaa.configurtaion.TokenConfig;
import cn.surkaa.module.domain.Todo;
import cn.surkaa.module.request.ResponseResult;
import cn.surkaa.module.request.TodoFlagRequest;
import cn.surkaa.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.surkaa.configurtaion.TokenConfig.MY_TOKEN;

/**
 * @author SurKaa
 */
@Slf4j
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 通过请求中的头部token获取对应用户的所有笔记
     *
     * @return 笔记列表
     */
    @GetMapping
    public ResponseResult<List<Todo>> getUserTodoList(
            HttpServletRequest request
    ) {
        String token = request.getHeader(MY_TOKEN);
        log.debug("get todolist, token: {}", token);
        Long userId = TokenConfig.getLoginId(token);
        return ResponseResult.succeed(todoService.getAllTodoByToken(userId));
    }

    @PostMapping
    public ResponseResult<Long> saveTodo(
            @RequestBody Todo todo,
            HttpServletRequest request
    ) {
        String token = request.getHeader(MY_TOKEN);
        Long userId = TokenConfig.getLoginId(token);
        log.debug("saveTodo: userId={}, todo={}", userId, todo);
        return ResponseResult.succeed(
                todoService.saveTodoWithToken(userId, todo)
        );
    }

    @PostMapping("/flag")
    public ResponseResult<Boolean> flagTodo(
            @RequestBody TodoFlagRequest todoFlag,
            HttpServletRequest request
    ) {
        String token = request.getHeader(MY_TOKEN);
        Long userId = TokenConfig.getLoginId(token);
        log.debug("flagTodo: todoFlag={}, userId={}", todoFlag, userId);
        return ResponseResult.succeed(
                todoService.flagTodo(userId, todoFlag)
        );
    }

}
