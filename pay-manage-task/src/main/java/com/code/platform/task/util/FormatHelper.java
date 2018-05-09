package com.code.platform.task.util;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FormatHelper功能主要实现<br/> 1、从String的数据到需要的类型JAVA数据对象的转换<br/>
 * 2、格式化JAVA数据对象为要求的String格式串<br/>
 */
public class FormatHelper {
    /**
     * 调试标志
     */
    public static final boolean m_debug = false;

    /**
     * 得到系统当前时间
     */
    public static Timestamp getSystemCurrentTimestamp() {
        java.util.Date date = new java.util.Date();

        java.text.DateFormat df = java.text.DateFormat.getDateTimeInstance();

        debug("Current Datetime is:" + df.format(date));

        return Timestamp.valueOf(df.format(date));
    }

    /**
     * 格式化时间戳<br/>
     * <p/>
     * 日期时间格式：如，2002-02-02 00:00:00
     *
     * @param time 时间戳
     *
     * @return 格式化时间字符串
     */
    public static String formatTimestamp(Timestamp time) {
        try {
            return time.toString().substring(0, 19);
        } catch (Exception e) {
        }

        return null;
    }

    /**
     * 格式化时间戳<br/>
     * <p/>
     * 可返回两种格式，日期格式：如，2002-02-02；<br/>日期时间格式：如，2002-02-02 00:00:00
     *
     * @param time  时间戳
     * @param bDate 返回日期格式标志。
     *
     * @return 格式化时间字符串
     */
    public static String formatTimestamp(Timestamp time, boolean bDate) {
        try {
            int n = 16;
            if (bDate) {
                n = 10;
            }
            return time.toString().substring(0, n);
        } catch (Exception e) {
        }

        return "";
    }

    /**
     * 根据时间戳字符串获取Tiestamp对象<br/>
     * <p/>
     * 输入：2002-02-02 或者 2002-02-02 00:00:00
     *
     * @param timestamp 时间格式或者日期格式
     *
     * @return Timestamp对象
     */
    public static Timestamp parseTimestamp(String timestamp) {
        try {
            return Timestamp.valueOf(timestamp);
        } catch (Exception e) {
            try {
                return Timestamp.valueOf(timestamp.trim() + " 00:00:00");
            } catch (Exception ee) {
                try {
                    return Timestamp.valueOf(timestamp.trim() + ":00");
                } catch (Exception eee) {
                    try {
                        return Timestamp.valueOf(timestamp.trim() + ":00:00");
                    } catch (Exception eeee) {
                    }
                }
            }
        }

        return null;
    }

    /**
     * 获取Tiestamp<br/>
     * <p/>
     * 输入：2002-02-02 或者 2002-02-02 00:00:00
     *
     * @param timestamp 时间格式或者日期格式
     */
    public static Timestamp parseTimestamp(String timestamp, boolean bStart) {
        if (bStart) {
            return parseTimestamp(timestamp);
        }

        try {
            return Timestamp.valueOf(timestamp);
        } catch (Exception e) {
            try {
                return Timestamp.valueOf(timestamp.trim() + " 23:59:59");
            } catch (Exception ee) {
            }
        }

        return null;
    }

    /**
     * 使用中文的"是","否"作为bollean型的true和false
     */
    public static final int BOOLEAN_CHINESE = 1;

    /**
     * 使用"Yes" "No"作为bollean型的true和false
     */
    public static final int BOOLEAN_YESNO = 2;

    /**
     * 使用"Y", "N"作为bollean型的true和false
     */
    public static final int BOOLEAN_YN = 3;

    /**
     * 使用"true", "false"作为bollean型的true和false
     */
    public static final int BOOLEAN_TRUEFALSE = 4;

    /**
     * 将中英文的字符串转换为boolean型
     *
     * @param strBoolean 需要转换的字符串
     * @param flag       转换类型, 转换类型--转换结果 <br/>有四种
     *                   BOOLEAN_CHINESE--是/否,BOOLEAN_YESNO--YES/NO,<br/>
     *                   BOOLEAN_YN--Y/N, BOOLEAN_TRUEFALSE--true/false
     *
     * @return 转换完的结果
     */
    public static boolean parseBoolean(String strBoolean, int flag) {
        boolean b = false;

        switch (flag) {
            case BOOLEAN_CHINESE:

                if (strBoolean.trim().equalsIgnoreCase("是")) {
                    b = true;
                }
                break;
            case BOOLEAN_YESNO:
                if (strBoolean.trim().equalsIgnoreCase("YES")) {
                    b = true;
                }
                break;
            case BOOLEAN_YN:
                if (strBoolean.trim().equalsIgnoreCase("Y")) {
                    b = true;
                }
                break;
            case BOOLEAN_TRUEFALSE:
                if (strBoolean.trim().equalsIgnoreCase("true")) {
                    b = true;
                }
                break;
        }

        return b;
    }

    /**
     * 将boolean型转换为中英文的字符串
     *
     * @param b    需要转换的boolean型
     * @param flag 转换类型, 转换类型--转换结果 <br/>有四种<br/>
     *             BOOLEAN_CHINESE--是/否,BOOLEAN_YESNO--YES/NO,<br/>
     *             BOOLEAN_YN--Y/N, BOOLEAN_TRUEFALSE--true/false
     *
     * @return 转换完的结果
     */
    public static String formatBoolean(boolean b, int flag) {
        String strBoolean = null;

        switch (flag) {
            case BOOLEAN_CHINESE:
                if (b) {
                    strBoolean = "是";
                } else {
                    strBoolean = "否";
                }
                break;
            case BOOLEAN_YESNO:
                if (b) {
                    strBoolean = "YES";
                } else {
                    strBoolean = "NO";
                }
                break;
            case BOOLEAN_YN:
                if (b) {
                    strBoolean = "Y";
                } else {
                    strBoolean = "N";
                }
                break;
            case BOOLEAN_TRUEFALSE:
                if (b) {
                    strBoolean = "true";
                } else {
                    strBoolean = "false";
                }
                break;
        }

        return strBoolean;
    }

    /**
     * 格式化BigDecimal
     *
     * @param decimal
     *
     * @return BigDecimal对象转换成的字符串
     */
    public static String formatBigDecimal(BigDecimal decimal) {
        return decimal.toString();
    }

    /**
     * 获取BigDecimal
     *
     * @param bigDecimal
     */
    public static BigDecimal parseBigDecimal(String bigDecimal) {
        try {
            return new BigDecimal(bigDecimal);
        } catch (Exception e) {
        }

        return null;
    }

    /**
     * 打印调试信息
     */
    public static void debug(String string) {
        if (m_debug) {
            System.out.println(string);
        }
    }

    /**
     * 打印调试信息
     */
    public static void debug(Object o) {
        if (m_debug) {
            System.out.println(o);
        }
    }

    /**
     * 格式化打折率
     *
     * @param discount String 打折率
     *
     * @return String 打折标志
     */
    public static String getDiscountFlag(String discount) {

        // 变汉语数字,例如"九",为阿拉伯数字9
        String qDiscount = qualifyDiscount(discount);
        // 删除非数字汉字,例如"折"
        StringBuffer discountSB = new StringBuffer(qDiscount);

        for (int i = 0; i < discountSB.length(); i++) {
            char c = discountSB.charAt(i);
            if (c < '1' || c > '9') {
                discountSB = discountSB.deleteCharAt(i);
                i--;
            }
        }

        return discountSB.toString();
    }

    /**
     * 变汉语数字,例如"九",为阿拉伯数字9
     *
     * @param discount String 打折
     *
     * @return String 打折标志
     */
    private static String qualifyDiscount(String discount) {

        if (discount.indexOf("九") != -1) {
            discount = discount.replace('九', '9');
        }
        if (discount.indexOf("八") != -1) {
            discount = discount.replace('八', '8');
        }
        if (discount.indexOf("七") != -1) {
            discount = discount.replace('七', '7');
        }
        if (discount.indexOf("六") != -1) {
            discount = discount.replace('六', '6');
        }
        if (discount.indexOf("五") != -1) {
            discount = discount.replace('五', '5');
        }
        if (discount.indexOf("四") != -1) {
            discount = discount.replace('四', '4');
        }
        if (discount.indexOf("三") != -1) {
            discount = discount.replace('三', '3');
        }
        if (discount.indexOf("二") != -1) {
            discount = discount.replace('二', '2');
        }
        if (discount.indexOf("一") != -1) {
            discount = discount.replace('一', '1');
        }
        return discount;
    }

    /**
     * 查询操作所使用的关键字中的%、[、_进行转义操作
     *
     * @param str 字符串多为查询的关键字
     *
     * @return 转义完的字符串
     */
    public static String sqlUnEscape(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }

        str = StringReplace.getReplaceString(str, "!'", "'");
        str = StringReplace.getReplaceString(str, "!%", "%");
        str = StringReplace.getReplaceString(str, "!!", "!");
        str = StringReplace.getReplaceString(str, "![", "[");
        str = StringReplace.getReplaceString(str, "!_", "_");
        str = StringReplace.getReplaceString(str, "!^", "^");
        str = StringReplace.getReplaceString(str, "\r", "");
        str = StringReplace.getReplaceString(str, "/r", "");
        return str;
    }

    /**
     * 查询操作所使用的关键字中的%、[、_进行转义操作
     *
     * @param str 字符串多为查询的关键字
     *
     * @return 转义完的字符串
     */
    public static String sqlEscape(String str) {
        String escapeStr = "";

        if (str != null && str.trim().length() > 0) {
            for (; ; ) {
                int index = str.indexOf("!");
                if (index == -1) {
                    escapeStr += str;
                    break;
                }
                escapeStr += str.substring(0, index) + "!!";
                str = str.substring(index + 1, str.length());
            }
            str = escapeStr;
            escapeStr = "";

            for (; ; ) {
                int index = str.indexOf("%");
                if (index == -1) {
                    escapeStr += str;
                    break;
                }
                escapeStr += str.substring(0, index) + "!%";
                str = str.substring(index + 1, str.length());
            }

            str = escapeStr;
            escapeStr = "";
            for (; ; ) {
                int index = str.indexOf("[");
                if (index == -1) {
                    escapeStr += str;
                    break;
                }
                escapeStr += str.substring(0, index) + "![";
                str = str.substring(index + 1, str.length());
            }

            str = escapeStr;
            escapeStr = "";

            for (; ; ) {
                int index = str.indexOf("_");
                if (index == -1) {
                    escapeStr += str;
                    break;
                }
                escapeStr += str.substring(0, index) + "!_";
                str = str.substring(index + 1, str.length());
            }

            str = escapeStr;
            escapeStr = "";

            for (; ; ) {
                int index = str.indexOf("'");
                if (index == -1) {
                    escapeStr += str;
                    break;
                }
                escapeStr += str.substring(0, index) + "!'";
                str = str.substring(index + 1, str.length());
            }

            str = escapeStr;
            escapeStr = "";

            for (; ; ) {
                int index = str.indexOf("^");
                if (index == -1) {
                    escapeStr += str;
                    break;
                }
                escapeStr += str.substring(0, index) + "!^";
                str = str.substring(index + 1, str.length());
            }
        }

        return escapeStr;
    }

    public static double getDouble(double data, int scale) {
        return new BigDecimal(data).setScale(scale, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    /**
     * 获取完整的URL
     *
     * @param request
     *
     * @return
     */
    public static String getRequestURL(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String url = "";
        url = request.getContextPath();
        url = url + request.getServletPath();

        java.util.Enumeration names = request.getParameterNames();
        int i = 0;
        String requestPageUrl = "";
        if (!StringUtils.isEmpty(request.getQueryString())) {
            url = url + "?" + request.getQueryString();
        }

        // if (names != null) {
        // while (names.hasMoreElements()) {
        // String name = (String) names.nextElement();
        // if (name.equals("requestPageUrl") || name.indexOf("log") != -1) {
        // requestPageUrl = request.getParameter(name);
        // continue;
        // }
        //
        // if (i == 0) {
        // url = url + "?";
        // } else {
        // url = url + "&";
        // }
        // i++;
        //
        // String value = request.getParameter(name);
        // if (value == null) {
        // value = "";
        // }
        //
        // url = url + name + "=" + value;
        // try {
        // // java.net.URLEncoder.encode(url, "ISO-8859");
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // }
        // if(!StringUtils.isEmpty(requestPageUrl)) {
        // if(url.indexOf("?") != -1) {
        // url = url + "&";
        // } else {
        // url = url + "?";
        // }
        //
        // url = url + "requestPageUrl=" + requestPageUrl;
        // }

        String enUrl = "";

        try {
            enUrl = java.net.URLEncoder.encode(url, "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return enUrl;
    }

    /**
     * 获取完整的URL
     *
     * @param request
     *
     * @return
     */
    public static String getNoEncodeRequestURL(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        StringBuffer url = new StringBuffer("http://");
        url.append(request.getServerName());
        url.append(":").append(request.getServerPort());
        url.append("/").append(request.getContextPath());
        url.append(request.getServletPath());

        // java.util.Enumeration<?> names = request.getParameterNames();
        // int i = 0;
        // String requestPageUrl = "";
        if (!StringUtils.isEmpty(request.getQueryString())) {
            url.append("?").append(request.getQueryString());
        }

        return FormatHelper.formatWebPath(url.toString());
    }

	/*
	 * public static void main(String[] args) { Timestamp timestamp =
	 * Timestamp.valueOf("2002-2-12 00:00:00"); Timestamp t =
	 * FormatHelper.parseTimestamp(" 2002-2-12 "); FormatHelper.debug("Current
	 * Time:");
	 * FormatHelper.debug(FormatHelper.formatTimestamp(FormatHelper.getSystemCurrentTimestamp()));
	 * java.math.BigDecimal g = new BigDecimal("12.000000");
	 * FormatHelper.debug(g); }
	 */

    /**
     * 按照指定长度截取中英文字符串
     *
     * @param str  需要截取的字符串
     * @param size 指定长度，为字节的长度，按照中文2个英文数字1个计算
     *
     * @return
     */
    public static String substring(String str, int size) {

        if (str == null || str.length() < 1 || size <= 2) {
            return str;
        }

        if (str.length() > 1 && size == 2) {
            int i = 0;
        }
        String returnStr = "";

        for (int i = 0, j = 0; j < size && j < str.getBytes().length; ) {
            String ch = str.charAt(i++) + "";

            if (returnStr.getBytes().length == size - 1
                    && ch.getBytes().length > 1) {
                returnStr = returnStr + ".";
            } else {
                returnStr = returnStr + ch;
            }

            j = j + ch.getBytes().length;
        }

        return returnStr;
    }

    /**
     * 按照指定长度截取中英文字符串
     *
     * @param str  需要截取的字符串
     * @param size 指定长度，为字节的长度，按照中文2个英文数字1个计算
     *
     * @return
     */
    public static String substring2(String str, int size) {

        if (str == null || str.length() < 1 || size <= 2) {
            return str;
        }

        if (str.length() > 1 && size == 2) {
            int i = 0;
        }
        String returnStr = "";

        for (int i = 0, j = 0; j < size && j < str.getBytes().length; ) {
            String ch = str.charAt(i++) + "";

            if (returnStr.getBytes().length == size - 1
                    && ch.getBytes().length > 1) {
                returnStr = returnStr + ".";
            } else {
                returnStr = returnStr + ch;
            }

            j = j + ch.getBytes().length;
        }

        return returnStr;
    }

    /**
     * 按照指定长度截取中英文字符串
     *
     * @param str  需要截取的字符串
     * @param size 指定长度，为字节的长度，按照中文2个英文数字1个计算
     *
     * @return
     */
    public static String substring(String str, int size, boolean isadd) {
        if (!isadd || (str != null && str.getBytes().length >= size)) {
            return substring(str, size);
        }

        int ensize = size - str.getBytes().length;

        for (int i = 0; i < ensize; i = i + 2) {
            str = str + "";
        }

        return str;
    }

    public static String filterHtml(String str) {

        if (StringUtils.isEmpty(str)) {
            return "";
        }

        Pattern pattern = Pattern
                .compile("<[^<|>]*>", Pattern.CASE_INSENSITIVE);
        Pattern pattern1 = Pattern.compile("&nbsp;", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);

        String returnStr = matcher.replaceAll("");

        Matcher matcher1 = pattern.matcher(returnStr);
        return returnStr;
    }

    public static String conertRtToBr(String str) {

        if (StringUtils.isEmpty(str)) {
            return "";
        }

        Pattern pattern = Pattern.compile("\n", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);

        String returnStr = matcher.replaceAll("<br/>");
        return returnStr;
    }

    public static List filterHtmlImgPath(String htmlStr) {
        if (StringUtils.isEmpty(htmlStr)) {
            return null;
        }
        String regx = "userfiles/product/img/[0-9]{8}/editor/\\w*\\.(jpg|jpeg|png|gif)";
        Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlStr);
        List returnStr = new ArrayList();

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            returnStr.add(htmlStr.substring(start, end));
        }
        return returnStr;
    }


    public static List filterHtmlImgPathStartWithHttp(String htmlStr) {
        if (StringUtils.isEmpty(htmlStr)) {
            return null;
        }

        String reg = "src=('|\"|.)http://";
        reg = reg + "(.*?)\\.(jpg|jpeg|gif|png|bmp)(.*?)('|\")";

        System.out.println(reg);
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(htmlStr);
        List returnStr = new ArrayList();

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            returnStr.add(htmlStr.substring(start, end));
        }
        return returnStr;
    }

    public static List filterHtmlLink(String htmlStr) {
        if (StringUtils.isEmpty(htmlStr)) {
            return null;
        }
        String regx = "<a (.*?)>(.*?)</a>";
        Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlStr);

        List returnStr = new ArrayList();
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            returnStr.add(htmlStr.substring(start, end));
        }

        return returnStr;
    }

    public static boolean filterHtmlIsExistedLink(String htmlStr,
                                                  String serverName) {
        if (StringUtils.isEmpty(htmlStr)) {
            return false;
        }
        String regx = "href=(\"|')" + serverName + "[/*|/*#*|\\*|\\*#*]\"";
        Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlStr);
        while (matcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * 格式化网站路径。将类似http://www.xxx.net//bg/\\user/\\，格式化成http://www.xxx.net/bg/user。注意：最后没有/。
     *
     * @param webPath
     *
     * @return
     */
    public static String formatWebPath(String webPath) {
        for (; webPath.indexOf("\\") > -1; ) {
            webPath = webPath.replace("\\", "/");
        }
        for (; webPath.indexOf("//") > -1; ) {
            webPath = webPath.replace("//", "/");
        }
        webPath = webPath.replace(":/", "://");
        if (webPath.endsWith("/")) {
            webPath = webPath.substring(0, webPath.length());
        }
        return webPath;
    }

    /**
     * 将list对象中的值， 转化成map
     *
     * @param list
     *
     * @return
     */
    public static Map<Object, Object> listToMap(List<?> list) {
        Map<Object, Object> map = new HashMap<Object, Object>();

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Object v = (Object) list.get(i);
                map.put(v, v);
            }
        }

        return map;
    }

    /**
     * 将 map 对象中的值， 转化成 list
     *
     * @param map
     *
     * @return
     */
    public static List<Object> mapToList(Map<?, ?> map) {
        List<Object> list = new ArrayList<Object>();

        if (map != null && map.size() > 0) {
            Iterator<?> its = map.keySet().iterator();
            while (its.hasNext()) {
                Object v = (Object) its.next();
                list.add(v);
            }
        }

        return list;
    }

    /**
     * 将数组， 转化成map
     *
     * @param array
     *
     * @return
     */
    public static Map<Object, String> arrayToMap(Object[] array) {
        Map<Object, String> map = new HashMap<Object, String>();

        if (array != null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                Object v = (Object) array[i];
                map.put(v, "");
            }
        }

        return map;
    }

    /**
     * 将数组， 转化成list
     *
     * @param array
     *
     * @return
     */
    public static List<Object> arrayToList(Object[] array) {
        List<Object> list = new ArrayList<Object>();

        if (array != null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                Object v = (Object) array[i];
                list.add(v);
            }
        }

        return list;
    }

    /**
     * 将格式为"value1;value2;value3"这样格式的字符串转化为Map[value1,value2,value3]
     *
     * @param map
     * @param paramStr
     * @param split1
     *
     * @return
     */
    public static Map<String, String> getParamsMap(Map<String, String> map,
                                                   String paramStr, String split1) {
        // 获取商品规格参数中输入的值
        if (!StringUtils.isEmpty(paramStr)) {
            String[] mp = paramStr.split(split1);

            if (mp != null && mp.length > 0) {
                for (int i = 0; i < mp.length; i++) {
                    String m = (String) mp[i];
                    map.put(m, m);
                }
            }
        }

        return map;
    }

    /**
     * 将格式为"name1,value1;name2,value2;name3,value3"这样格式的字符串转化为Map[name1=value1;name2=value2;name3=value3]
     *
     * @param map
     * @param paramStr
     * @param split1
     *
     * @return
     */
    public static Map<String, String> getParamsMap(Map<String, String> map,
                                                   String paramStr, String split1, String split2) {
        // 获取商品规格参数中输入的值
        if (!StringUtils.isEmpty(paramStr)) {
            String[] mp = paramStr.split(split1);

            if (mp != null && mp.length > 0) {
                for (int i = 0; i < mp.length; i++) {
                    String m = (String) mp[i];

                    String[] v = m.split(split2);

                    if (v.length == 2) {
                        map.put(v[0], v[1]);
                    }
                }
            }
        }

        return map;
    }

    /**
     * 将格式为"name1,value1;name2,value2;name3,value3"这样格式的字符串转化为Map[name1=value1;name2=value2;name3=value3]
     *
     * @param request
     * @param paramName
     * @param split
     *
     * @return
     */
    public static String getParamsValue(HttpServletRequest request,
                                        String paramName, String split) {
        String[] values = request.getParameterValues(paramName);

        StringBuffer sb = new StringBuffer();
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    sb.append(split);
                }

                String v = values[i];
                sb.append(v);
            }
        }

        return sb.toString();
    }

    @SuppressWarnings("unused")
    public static String getRequestURLNoEncoder(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        StringBuffer url = new StringBuffer();
        url.append(request.getContextPath());
        url.append(request.getServletPath());

        java.util.Enumeration<?> names = request.getParameterNames();
        int i = 0;
        String requestPageUrl = "";
        if (!StringUtils.isEmpty(request.getQueryString())) {
            url.append("?").append(request.getQueryString());
        }
        String enUrl = "";

        try {
            enUrl = url.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return enUrl;
    }
}
