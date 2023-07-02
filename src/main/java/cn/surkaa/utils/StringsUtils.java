package cn.surkaa.utils;

import cn.hutool.core.util.CharUtil;

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

}
