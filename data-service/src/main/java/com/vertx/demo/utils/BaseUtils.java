package com.vertx.demo.utils;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.ArrayTuple;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangls on 2020/7/6.
 *
 * @author zhangls
 */
public class BaseUtils {

    private static Pattern charPattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
    private static Pattern spePattern = Pattern.compile("([A-Za-z\\d]+)(_)?");

    /**
     * 返回当前执行方法的名称
     *
     * @param isExec
     * @return true：执行这个方法的名称 false：被执行的方法的名称
     */
    public static String getMethodName(Boolean isExec) {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();

        return isExec ? stackTrace[0].getMethodName() : stackTrace[1].getMethodName();
    }

    public static Tuple crtTuple(JsonObject params) {
        if (null == params) {
            return null;
        }

        Map<String, Object> map = params.getMap();

        Tuple tuple = new ArrayTuple(map.size());
        for (int i = 0; i < params.size(); i++) {
            tuple.addValue(map.get(i));
        }

        return tuple;
    }

    /**
     * 下划线转驼峰法
     *
     * @param line       源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line, boolean smallCamel) {
        if (line == null || "".equals(line)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();

        Matcher matcher = spePattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(smallCamel && matcher.start() == 0 ? Character.toLowerCase(word.charAt(0)) : Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰法转下划线
     *
     * @param line 源字符串
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line) {
        if (line == null || "".equals(line)) {
            return "";
        }
        line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuffer sb = new StringBuffer();

        Matcher matcher = charPattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end() == line.length() ? "" : "_");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String line = "USER_ID";
        String camel = underline2Camel(line, false);
        System.out.println(camel);
        System.out.println(camel2Underline(camel));
    }

}
