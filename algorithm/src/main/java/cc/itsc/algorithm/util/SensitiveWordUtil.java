package cc.itsc.algorithm.util;

import java.io.*;
import java.util.*;

public class SensitiveWordUtil {
    /**
     * 敏感词匹配规则
     * 最小匹配规则，如：敏感词库["中国","中国人"]，语句："我是中国人"，匹配结果：我是[中国]人
     * 最大匹配规则，如：敏感词库["中国","中国人"]，语句："我是中国人"，匹配结果：我是[中国人]
     * 默认掩码
     */
    private static final int MinMatchType = 1;
    private static final int MaxMatchType = 2;
    private static final char maskChar = '*';

    /**
     * 敏感词集合
     */
    private static HashMap sensitiveWordMap;

    /**
     * 初始化敏感词库，构建DFA算法模型
     *
     * @param sensitiveWordSet 敏感词库
     */
    public static synchronized void init(Set<String> sensitiveWordSet) {
        initSensitiveWordMap(sensitiveWordSet);
    }

    /**
     * 初始化敏感词库，构建DFA算法模型
     *
     * @param sensitiveWordSet 敏感词库
     */
    private static void initSensitiveWordMap(Set<String> sensitiveWordSet) {
        //初始化敏感词容器，减少扩容操作
        sensitiveWordMap = new HashMap(sensitiveWordSet.size());
        String key;
        Map nowMap;
        Map<String, String> newWorMap;
        //迭代sensitiveWordSet
        for (String s : sensitiveWordSet) {
            //关键字
            key = s.toLowerCase();
            nowMap = sensitiveWordMap;
            for (int i = 0; i < key.length(); i++) {
                //转换成char型
                char keyChar = key.charAt(i);
                //库中获取关键字
                Object wordMap = nowMap.get(keyChar);
                //如果存在该key，直接赋值，用于下一个循环获取
                if (wordMap != null) {
                    nowMap = (Map) wordMap;
                } else {
                    //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                    newWorMap = new HashMap<>();
                    //不是最后一个
                    newWorMap.put("isEnd", "0");
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                if (i == key.length() - 1) {
                    //最后一个
                    nowMap.put("isEnd", "1");
                }
            }
        }
    }

    /**
     * 判断文字是否包含敏感字符
     *
     * @param txt       文字
     * @param matchType 匹配规则 1：最小匹配规则，2：最大匹配规则
     * @return 若包含返回true，否则返回false
     */
    public static boolean contains(String txt, int matchType) {
        boolean flag = false;
        for (int i = 0; i < txt.length(); i++) {
            //判断是否包含敏感字符
            int matchFlag = checkSensitiveWord(txt, i, matchType);
            //大于0存在，返回true
            if (matchFlag > 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 判断文字是否包含敏感字符
     *
     * @param txt 文字
     * @return 若包含返回true，否则返回false
     */
    public static boolean contains(String txt) {
        return contains(txt, MaxMatchType);
    }

    /**
     * 获取文字中的敏感词
     *
     * @param txt       文字
     * @param matchType 匹配规则 1：最小匹配规则，2：最大匹配规则
     * @return 文字中的敏感词
     */
    public static Set<String> getSensitiveWord(String txt, int matchType) {
        Set<String> sensitiveWordList = new HashSet<>();

        for (int i = 0; i < txt.length(); i++) {
            //判断是否包含敏感字符
            int length = checkSensitiveWord(txt, i, matchType);
            //存在,加入list中
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));
                //减1的原因，是因为for会自增
                i = i + length - 1;
            }
        }
        return sensitiveWordList;
    }

    /**
     * 获取文字中的敏感词
     *
     * @param txt 文字
     * @return 文字中的敏感词
     */
    public static Set<String> getSensitiveWord(String txt) {
        return getSensitiveWord(txt, MaxMatchType);
    }

    /**
     * 替换敏感字字符
     *
     * @param txt         文本
     * @param replaceChar 替换的字符，匹配的敏感词以字符逐个替换，如 语句：我爱中国人 敏感词：中国人，替换字符：*， 替换结果：我爱***
     * @param matchType   敏感词匹配规则
     * @return 替换后的参数文本
     */
    public static String replaceSensitiveWord(String txt, char replaceChar, int matchType) {
        String resultTxt = txt;
        //获取所有的敏感词
        Set<String> set = getSensitiveWord(txt, matchType);
        Iterator<String> iterator = set.iterator();
        String word;
        String replaceString;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }

        return resultTxt;
    }

    /**
     * 替换敏感字字符
     *
     * @param txt         文本
     * @param replaceChar 替换的字符，匹配的敏感词以字符逐个替换，如 语句：我爱中国人 敏感词：中国人，替换字符：*， 替换结果：我爱***
     * @return 替换后的参数文本
     */
    public static String replaceSensitiveWord(String txt, char replaceChar) {
        return replaceSensitiveWord(txt, replaceChar, MaxMatchType);
    }

    /**
     * 替换敏感字字符
     *
     * @param txt        文本
     * @param replaceStr 替换的字符串，匹配的敏感词以字符逐个替换，如 语句：我爱中国人 敏感词：中国人，替换字符串：[屏蔽]，替换结果：我爱[屏蔽]
     * @param matchType  敏感词匹配规则
     * @return 替换后的参数文本
     */
    public static String replaceSensitiveWord(String txt, String replaceStr, int matchType) {
        String resultTxt = txt;
        //获取所有的敏感词
        Set<String> set = getSensitiveWord(txt, matchType);
        Iterator<String> iterator = set.iterator();
        String word;
        while (iterator.hasNext()) {
            word = iterator.next();
            resultTxt = resultTxt.replaceAll(word, replaceStr);
        }

        return resultTxt;
    }

    /**
     * 替换敏感字字符
     *
     * @param txt       文本
     * @param matchType 敏感词匹配规则
     */
    public static String replaceSensitiveWord(String txt, int matchType) {
        String resultTxt = txt;
        //获取所有的敏感词
        Set<String> set = getSensitiveWord(txt, matchType);
        Iterator<String> iterator = set.iterator();
        String word;
        while (iterator.hasNext()) {
            word = iterator.next();
            resultTxt = resultTxt.replaceAll(word, getReplaceChars(maskChar, word.length()));
        }

        return resultTxt;
    }

    /**
     * 替换敏感字字符
     *
     * @param txt        文本
     * @param replaceStr 替换的字符串，匹配的敏感词以字符逐个替换，如 语句：我爱中国人 敏感词：中国人，替换字符串：[屏蔽]，替换结果：我爱[屏蔽]
     * @return 替换后的参数文本
     */
    public static String replaceSensitiveWord(String txt, String replaceStr) {
        return replaceSensitiveWord(txt, replaceStr, MaxMatchType);
    }

    /**
     * 获取替换字符串
     *
     * @param replaceChar 替换使用的关键词
     * @param length      替换的长度
     * @return 替换后的字符串
     */
    private static String getReplaceChars(char replaceChar, int length) {
        StringBuilder resultReplace = new StringBuilder(String.valueOf(replaceChar));
        for (int i = 1; i < length; i++) {
            resultReplace.append(replaceChar);
        }
        return resultReplace.toString();
    }

    /**
     * 检查文字中是否包含敏感字符，检查规则如下：<br>
     *
     * @param txt        需要检查的文本
     * @param beginIndex 需要检查的文本的起点位置
     * @param matchType  敏感词的匹配规则
     * @return 如果存在，则返回敏感词字符的长度，不存在返回0
     */
    private static int checkSensitiveWord(String txt, int beginIndex, int matchType) {
        //敏感词结束标识位：用于敏感词只有1位的情况
        boolean flag = false;
        //匹配标识数默认为0
        int matchFlag = 0;
        char word;
        Map nowMap = sensitiveWordMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            word = txt.charAt(i);
            //获取指定key
            nowMap = (Map) nowMap.get(word);
            //存在，则判断是否为最后一个
            if (nowMap != null) {
                //找到相应key，匹配标识+1
                matchFlag++;
                //如果为最后一个匹配规则,结束循环，返回匹配标识数
                if ("1".equals(nowMap.get("isEnd"))) {
                    //结束标志位为true
                    flag = true;
                    //最小规则，直接返回,最大规则还需继续查找
                    if (MinMatchType == matchType) {
                        break;
                    }
                }
            } else {
                //不存在，直接返回
                break;
            }
        }
        if (matchFlag < 2 || !flag) {
            //长度必须大于等于1，为词
            matchFlag = 0;
        }
        return matchFlag;
    }


    /**
     * 初始化敏感词库
     */
    public static Set<String> InitializationWork(String path) {
        Set<String> SensitiveKeys = new HashSet<>();
        try (InputStreamReader read = new InputStreamReader(
                new FileInputStream(path))){
            BufferedReader bufferedReader = null;
            bufferedReader = new BufferedReader(read);
            for (String txt = null; (txt = bufferedReader.readLine()) != null; ) {
                SensitiveKeys.add(txt);
            }
            return SensitiveKeys;
        } catch (IOException e) {
            return Collections.emptySet();
        }
    }


    public static void main(String[] args) {
        //初始化敏感词库
        SensitiveWordUtil.init(InitializationWork("E:\\GitHub\\UpPlan7\\algorithm\\src\\main\\java\\cc\\itsc\\algorithm\\util\\SensitiveKeys.txt"));

        System.out.println("敏感词的数量：" + SensitiveWordUtil.sensitiveWordMap.size());
        String string = "中华人民共和国万岁，伟大的共产党万岁，中央人民政府万岁。万岁万岁万万岁。我操你妈，苍井空空哈哈";
        System.out.println("待检测语句字数：" + string.length());

        //是否含有关键字
        boolean result = SensitiveWordUtil.contains(string);
        System.out.println(result);
        result = SensitiveWordUtil.contains(string, SensitiveWordUtil.MaxMatchType);
        System.out.println(result);

        //获取语句中的敏感词
        Set<String> set = SensitiveWordUtil.getSensitiveWord(string);
        System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
        set = SensitiveWordUtil.getSensitiveWord(string, SensitiveWordUtil.MaxMatchType);
        System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);

        //替换语句中的敏感词
        String filterStr = SensitiveWordUtil.replaceSensitiveWord(string, '*');
        System.out.println(filterStr);
        filterStr = SensitiveWordUtil.replaceSensitiveWord(string, '*', SensitiveWordUtil.MaxMatchType);
        System.out.println(filterStr);

        String filterStr2 = SensitiveWordUtil.replaceSensitiveWord(string, "[*敏感词*]");
        System.out.println(filterStr2);
        filterStr2 = SensitiveWordUtil.replaceSensitiveWord(string, "[*敏感词*]", SensitiveWordUtil.MaxMatchType);
        System.out.println(filterStr2);

        //敏感词过滤实际上是:维护一个敏感词库，这里我用list模拟一个敏感词库
        List<String> list = new ArrayList<>();
        list.add("怒");
        list.add("自己");
        SensitiveWordUtil.init(new HashSet<>(list));
        String str = "自己太多的伤感情怒哀乐而自己过于牵强的把自己的情感adb也附加c于银幕情节中，然后感动就流泪";
        System.out.println("--->替换前:" + str);
        System.out.println("--->替换后:" + SensitiveWordUtil.replaceSensitiveWord(str, 2));
    }
}
