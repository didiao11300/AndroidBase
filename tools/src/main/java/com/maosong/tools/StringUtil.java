package com.maosong.tools;

/**
 * create by colin on 2019/3/12
 */
public class StringUtil {
    public static String replaceLast(String text, String strToReplace,
                                     String replaceWithThis) {
        return text.replaceFirst("(?s)" + strToReplace + "(?!.*?" + strToReplace
                + ")", replaceWithThis);
    }
}
