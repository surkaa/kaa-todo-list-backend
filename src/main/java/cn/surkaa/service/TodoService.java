package cn.surkaa.service;

import cn.surkaa.module.Todo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author SurKaa
 * @description 针对表【todolist】的数据库操作Service
 * @createDate 2023-07-18 15:13:16
 */
public interface TodoService extends IService<Todo> {

    /**
     * 通过用户id查询用户的所有笔记
     *
     * @param userId 用户id
     * @return 笔记列表
     */
    List<Todo> getAllTodoByToken(Long userId);

}
