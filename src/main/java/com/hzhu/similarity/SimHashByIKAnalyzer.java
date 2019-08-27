package com.hzhu.similarity;

import com.hzhu.bean.SimilarityDto;
import com.hzhu.dao.CarMapper;
import com.hzhu.utils.ImportantWord;
import com.hzhu.utils.MybatisUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 通过IK分词器 汉明距离 计算文本的相似度
 * @Date 2019/8/27 9:46
 * @Created by CZB
 */
public class SimHashByIKAnalyzer {

    /**
     * 字符串
     */
    private String tokens;

    /**
     * 字符产的hash值
     */
    private BigInteger strSimHash;

    /**
     * 分词后的hash数
     */
    private int hashBits = 64;

    /**
     * 数字正则
     */
    private static final Pattern pattern = Pattern.compile("[0-9.]+");

    public SimHashByIKAnalyzer() {
    }

    public SimHashByIKAnalyzer(String tokens) {
        this.tokens = convertWord(tokens);
        this.strSimHash = this.simHash();
    }

    public SimHashByIKAnalyzer(String tokens, int hashBits) {
        this.tokens = convertWord(tokens);
        this.hashBits = hashBits;
        this.strSimHash = this.simHash();
    }

    /**
     * 对单个的分词进行hash计算;
     *
     * @param source
     * @return
     */
    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            /**
             * 当sourece 的长度过短，会导致hash算法失效，因此需要对过短的词补偿
             */
            while (source.length() < 3) {
                source = source + source.charAt(0);
            }
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").shiftLeft(this.hashBits - 1).subtract(new BigInteger("1"));

            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    /**
     * 清除html标签
     * @param content
     * @return
     */
    private String cleanResume(String content) {
        // 若输入为HTML,下面会过滤掉所有的HTML的tag
        content = Jsoup.clean(content, Whitelist.none());
        content = StringUtils.lowerCase(content);
        String[] strings = {" ", "\n", "\r", "\t", "\\r", "\\n", "\\t", "&nbsp;"};
        for (String s : strings) {
            content = content.replaceAll(s, "");
        }
        return content;
    }

    /**
     * 将同类型关键字转换
     * @param word
     * @return
     */
    private String convertWord(String word) {
        for (Map.Entry<String, List<String>> entry : ImportantWord.importantWordMap.entrySet()) {
            String key = entry.getKey();

            //待转换的值list
            List<String> list = entry.getValue();
            for (String s : list) {
                if (word.toUpperCase().indexOf(s) != -1) {
                    return word.replace(s, key);
                }
            }
        }
        return word;
    }

    /**
     * 汉明距离计算公式
     * @return
     */
    private BigInteger simHash() {

        tokens = cleanResume(tokens); // cleanResume 删除一些特殊字符

        int[] v = new int[this.hashBits];

        List<String> segments = IKAnalysisUtils.segment(this.tokens);


        Map<String, Integer> weightOfNature = new HashMap<>(); // 词性的权重


        //加大从配置文件中配置关键词的权重
        //加大数字的权重
        for (String keyWord : ImportantWord.importantWordList) {
            weightOfNature.put(keyWord, ImportantWord.weightMap.get(keyWord));
        }
        Matcher matcher = pattern.matcher(this.tokens);
        while (matcher.find()) {
            weightOfNature.put(matcher.group(), 5);
        }

        int overCount = 5; //设定超频词汇的界限 ;
        Map<String, Integer> wordCount = new HashMap<>();


        for (String word : segments) {
            //  过滤超频词
            if (wordCount.containsKey(word)) {
                int count = wordCount.get(word);
                if (count > overCount) {
                    continue;
                }
                wordCount.put(word, count + 1);
            } else {
                wordCount.put(word, 1);
            }


            // 2、将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数.
            BigInteger t = this.hash(word);
            for (int i = 0; i < this.hashBits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                // 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
                // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
                int weight = 1;  //添加权重

                for (String key : weightOfNature.keySet()) {
                    if (word.indexOf(key) != -1) {
                        weight = weightOfNature.get(key);
                    }
                }

                if (t.and(bitmask).signum() != 0) {
                    // 这里是计算整个文档的所有特征的向量和
                    v[i] += weight;
                } else {
                    v[i] -= weight;
                }
            }
        }

        BigInteger fingerprint = new BigInteger("0");
        for (int i = 0; i < this.hashBits; i++) {
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
            }
        }
        return fingerprint;
    }

    /**
     * 计算海明距离,海明距离越小说明越相似;
     *
     * @param other
     * @return
     */
    private int hammingDistance(SimHashByIKAnalyzer other) {
        BigInteger m = new BigInteger("1").shiftLeft(this.hashBits).subtract(
                new BigInteger("1"));
        BigInteger x = this.strSimHash.xor(other.strSimHash).and(m);

        int tot = 0;
        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }


    public double getSemblance(SimHashByIKAnalyzer s2) {
        double i = (double) this.hammingDistance(s2);
        return 1 - i / this.hashBits;
    }

    public List<SimilarityDto> getSimilarity(String addName, List<String> modelNames) {

        ArrayList<SimilarityDto> list = new ArrayList<>();

        SimHashByIKAnalyzer hash = new SimHashByIKAnalyzer(addName);

        for (String modelName : modelNames) {
            SimHashByIKAnalyzer hashN = new SimHashByIKAnalyzer(modelName);
            double similarity = hash.getSemblance(hashN);
            list.add(new SimilarityDto(modelName, similarity));
        }

        Collections.sort(list, (o1, o2) -> o2.getRatio().compareTo(o1.getRatio()));

        return list.subList(0, 3);
    }


    public static void main(String[] args) {

        String addModel = "2016款 2.0T 65周年限量型plus";

        SqlSession sqlSession = new MybatisUtils().getSqlSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        List<String> modelNames = mapper.getModelName();

        List<SimilarityDto> similarity = new SimHashByIKAnalyzer().getSimilarity(addModel, modelNames);
        System.out.println(similarity);

    }
}
