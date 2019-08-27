package com.hzhu.utils;

import com.hzhu.dao.CarMapper;
import com.hzhu.similarity.IKAnalysisUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * @Description TODO
 * @Date 2019/8/27 15:15
 * @Created by CZB
 */
public class ExtractDic {

    private String outPath = "dic.txt";

    private File outFile;

    private static Logger logger = LoggerFactory.getLogger(ExtractDic.class);

    public ExtractDic() {

        this.outFile = new File(outPath);
        if (outFile.exists()) {
            outFile.delete();
        }
    }

    private void write2file(String word) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(outFile));
            out.write(word);

        } catch (Exception e) {
            logger.error("ExtractDic.write2file error!", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("ExtractDic.write2file error!", e);
                }
            }
        }
    }

    public void extractWord() {
        SqlSession sqlSession = new MybatisUtils().getSqlSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        List<String> modelNames = mapper.getModelName();
        StringBuilder sb = new StringBuilder();
        for (String modelName : modelNames) {
            List<String> segment = IKAnalysisUtils.segment(modelName);
            for (String s : segment) {
                sb.append(s).append("\n");
            }
        }

        write2file(sb.toString());
        logger.debug("ExtractDic.extractWord success!");
    }

    public static void main(String[] args) {
        new ExtractDic().extractWord();
    }
}
