package cn.surkaa.controller;

import cn.surkaa.configurtaion.TokenConfig;
import cn.surkaa.module.domain.Todo;
import cn.surkaa.module.request.todo.*;
import cn.surkaa.module.utils.ResponseResult;
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
            @RequestBody TodoSaveBody saveBody,
            HttpServletRequest request
    ) {
        String token = request.getHeader(MY_TOKEN);
        Long userId = TokenConfig.getLoginId(token);
        log.debug("saveTodo: userId={}, saveBody={}", userId, saveBody);
        return ResponseResult.succeed(
                todoService.saveTodoWithToken(userId, saveBody)
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
                todoService.modifyFlagTodo(userId, todoFlag)
        );
    }

    @PostMapping("/title")
    public ResponseResult<Boolean> titleTodo(
            @RequestBody TodoTitleRequest todoTitle,
            HttpServletRequest request
    ) {
        String token = request.getHeader(MY_TOKEN);
        Long userId = TokenConfig.getLoginId(token);
        log.debug("flagTodo: todoTitle={}, userId={}", todoTitle, userId);
        return ResponseResult.succeed(
                todoService.modifyTitleTodo(userId, todoTitle)
        );
    }

    @PostMapping("/desc")
    public ResponseResult<Boolean> descTodo(
            @RequestBody TodoDescRequest todoDesc,
            HttpServletRequest request
    ) {
        String token = request.getHeader(MY_TOKEN);
        Long userId = TokenConfig.getLoginId(token);
        log.debug("flagTodo: todoDesc={}, userId={}", todoDesc, userId);
        return ResponseResult.succeed(
                todoService.modifyDescTodo(userId, todoDesc)
        );
    }

    @PostMapping("/target")
    public ResponseResult<Boolean> targetTodo(
            @RequestBody TodoTargetRequest todoTarget,
            HttpServletRequest request
    ) {
        String token = request.getHeader(MY_TOKEN);
        Long userId = TokenConfig.getLoginId(token);
        log.debug("flagTodo: todoTarget={}, userId={}", todoTarget, userId);
        return ResponseResult.succeed(
                todoService.modifyTargetTodo(userId, todoTarget)
        );
    }

}
