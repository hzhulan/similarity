package com.hzhu.similarity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description IK分词器工具
 * @Date 2019/8/26 17:02
 * @Created by CZB
 */
public class IKAnalysisUtils {

    private static MyConfiguration myCfg;

    private static Logger logger = LoggerFactory.getLogger(IKAnalysisUtils.class);


    static {
        myCfg = new MyConfiguration();
        myCfg.setUseSmart(true);
    }

    /**
     * 使用IK分词器分词
     * @param text
     * @return
     */
    public static List<String> segment(String text) {
        StringReader reader = new StringReader(text);
        IKSegmenter ik = new IKSegmenter(reader, myCfg);
        Lexeme lex = null;
        List<String> list = new ArrayList<>();
        try {
            while((lex=ik.next())!=null){
                list.add(lex.getLexemeText());
            }
        }catch (Exception e) {
            logger.error("IKAnalysisUtils.segment error!", e);
        }
        return list;
    }
}
