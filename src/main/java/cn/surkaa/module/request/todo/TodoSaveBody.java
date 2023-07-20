package cn.surkaa.module.request.todo;

import lombok.Data;

/**
 * @author SurKaa
 */
@Data
public class TodoSaveBody {

    String title;
    Long targetTime;
    String description;

}
