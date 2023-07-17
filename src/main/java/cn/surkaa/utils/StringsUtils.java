package cn.surkaa.utils;

import cn.hutool.core.util.CharUtil;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author SurKaa
 */
public class StringsUtils {

    /**
     * 判断字符串中是否含有存在空格的字符串
     * 是否存在不属于a
     * @param string 字符串
     * @return 当存在时返回true
     */
    public static boolean isNotBelongLatterAndNumber(String string) {
        for (char c : string.toCharArray()) {
            if (!CharUtil.isNumber(c) && !CharUtil.isLetter(c) && !CharUtil.isLetterUpper(c)) {
                return true;
            }
        }
        return false;
    }

    public static String md5DigestAsHex(Object o) {
        Objects.requireNonNull(o);
        return DigestUtils.md5DigestAsHex(
                String.valueOf(o).getBytes(StandardCharsets.UTF_8)
        );
    }

}
