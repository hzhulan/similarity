package com.hzhu.utils;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Date 2019/6/5 10:19
 * @Created by CZB
 */
public class ImportantWord {
    private static Document document;

    public static List<String> importantWordList;

    public static Map<String, List<String>> importantWordMap;

    private static int defaultWeight = 1;

    private static Logger logger = LoggerFactory.getLogger(ImportantWord.class);

    /**
     * 关键字权重
     */
    public static Map<String, Integer> weightMap;


    private ImportantWord() {
    }

    static {
        InputStream inputStream = ImportantWord.class.getClassLoader().getResourceAsStream("ikanalyzer/word.xml");
        SAXReader saxReader = new SAXReader();
        try {
            document = saxReader.read(inputStream);

            /**
             * 初始化，在取值的时候赋权重初值
             */
            weightMap = new HashMap<>();

            importantWordMap = getKeyWord();

            importantWordList = new ArrayList<>(importantWordMap.keySet());

        } catch (DocumentException e) {
            logger.error("ImportantWord.static initializer error!", e);
        }
    }

    /**
     * 获取索引存放路径
     * @return
     */
    private static Map<String, List<String>> getKeyWord() {
        Map<String, List<String>> wordMap = new HashMap<>();
        try {
            List<Element> list = document.selectNodes("root/word");
            for (Element element : list) {

                String key = element.attribute("value").getText();

                Attribute weightAttr = element.attribute("weight");
                int weight;
                if (weightAttr == null) {
                    weight = defaultWeight;
                } else {
                    weight = Integer.parseInt(weightAttr.getText());
                }

                //权重map赋值
                weightMap.put(key, weight);

                List<Element> items = element.selectNodes("item");


                List<String> wordList = new ArrayList<>();
                wordMap.put(key, wordList);
                for (Element item : items) {
                    String sameValue = item.getTextTrim().toUpperCase();
                    wordList.add(sameValue);
                }
            }


        } catch (NumberFormatException e) {
            logger.error("ImportantWord.getKeyWord error!", e);
        }

        return wordMap;
    }

    public static void main(String[] args) {
    }
}
