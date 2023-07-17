package cn.surkaa.module;

import lombok.Data;

/**
 * 保存至token的map中的实体
 *
 * @author SurKaa
 */
@Data
public class TokenInfo {
    private Long id;
    private Long time;

    public TokenInfo(Long id) {
        this.id = id;
        this.time = System.currentTimeMillis();
    }

    public Long getId() {
        this.time = System.currentTimeMillis();
        return id;
    }
}
