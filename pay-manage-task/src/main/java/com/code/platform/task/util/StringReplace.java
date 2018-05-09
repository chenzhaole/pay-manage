package com.code.platform.task.util;


public class StringReplace {

    private String sString;
    private String tString;
    private int lenOfsS;
    private int lenOftS;
    private String lString;
    private String rString;
    private int lenOfrS;

    /**
     * 说明：StringReplace构造函数
     */
    public StringReplace() {
    }

    /**
     * 说明：StringReplace构造函数
     *
     * @param sourceString  原字符串
     * @param targetString  需替换的源字符串
     * @param replaceString 替换的目标字符串
     */
    public StringReplace(String sourceString, String targetString, String replaceString) {
        sString = sourceString;
        tString = targetString;
        rString = replaceString;
        lenOfsS = sString.length();
        lenOftS = tString.length();
        lenOfrS = rString.length();
    }

    /**
     * 说明：输入原字符串
     *
     * @param sourceString 原字符串
     */
    public void setSourceString(String sourceString) {
        sString = sourceString;
        lenOfsS = sString.length();
    }

    /**
     * 说明：输入需要替换的字符串
     *
     * @param targetString 需要替换的原字符串
     */
    public void setTargetString(String targetString) {
        tString = targetString;
        lenOfsS = tString.length();
    }

    /**
     * 说明：输入替换字符串
     *
     * @param replaceString 替换的目标字符串
     */
    public void setReplaceString(String replaceString) {
        rString = replaceString;
        lenOfrS = rString.length();
    }

    /**
     * 说明：取得全部替换后的字符串
     *
     * @return String        替换后的字符串
     */
    public String getReplaceString() {
        lString = sString;
        int len = lString.length();
        String tempLeft;
        String tempMid;
        String tempRight;
        boolean matches = false;
        char rS = tString.charAt(0);
        int j = 0;
        for (int i = 0; i < len; i++) {
            if ((rS == lString.charAt(i)) &&
                    ((i + lenOftS) <= len)) {
                matches = tString.regionMatches(0, lString, i, lenOftS);
                if (matches) {
                    tempLeft = lString.substring(0, i);
                    tempMid = lString.substring(i, i + lenOftS);
                    tempMid = rString;
                    tempRight = lString.substring(i + lenOftS, len);
                    lString = tempLeft + tempMid + tempRight;
                    i = i + lenOfrS - 1;
                    len = lString.length();
                }

            }
        }
        return lString;
    }

    /**
     * 说明: 获得替换后的字符串
     *
     * @param sourceString  原字符串
     * @param targetString  被替换的字符串
     * @param replaceString 替换字符串
     *
     * @return String           替换后的字符串
     */
    public static String getReplaceString(String sourceString, String targetString,
                                          String replaceString) {
        String ssString = sourceString;
        String ttString = targetString;
        String rrString = replaceString;
        int lenOfsS = ssString.length();
        int lenOftS = ttString.length();
        int lenOfrS = rrString.length();

        if (lenOfsS <= 0 || lenOftS <= 0 || lenOfrS <= 0) {
            return sourceString;
        }
        String llString = ssString;
        int len = llString.length();
        String tempLeft;
        String tempMid;
        String tempRight;
        boolean matches = false;
        char rS = ttString.charAt(0);
        int j = 0;
        for (int i = 0; i < len; i++) {
            if ((rS == llString.charAt(i)) &&
                    ((i + lenOftS) <= len)) {
                matches = ttString.regionMatches(0, llString, i, lenOftS);
                if (matches) {
                    tempLeft = llString.substring(0, i);
                    tempMid = llString.substring(i, i + lenOftS);
                    tempMid = rrString;
                    tempRight = llString.substring(i + lenOftS, len);
                    llString = tempLeft + tempMid + tempRight;
                    i = i + lenOfrS - 1;
                    len = llString.length();
                }

            }
        }
        return llString;
    }

}
