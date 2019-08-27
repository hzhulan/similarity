package com.hzhu.similarity;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MyConfiguration implements Configuration {

    private static Logger logger = LoggerFactory.getLogger(MyConfiguration.class);

    // 懒汉单例
    private static final Configuration CFG = new MyConfiguration();
    /*
     * 分词器默认字典路径
     */
    private String PATH_DIC_MAIN = "ikanalyzer/model.dic";// 需要把static  final去掉
    private static final String PATH_DIC_QUANTIFIER = "ikanalyzer/quantifier.dic";
    /*
     * 分词器配置文件路径
     */
    private static final String FILE_NAME = "ikanalyzer/IKAnalyzer.cfg.xml";// 指定我们自己的分词器配置文件
    // 配置属性——扩展字典
    private static final String EXT_DICT = "ext_dict";
    // 配置属性——扩展停止词典
    private static final String EXT_STOP = "ext_stopwords";

    private Properties props;
    /*
     * 是否使用smart方式分词
     */
    private boolean useSmart;

    /**
     * 返回单例
     *
     * @return Configuration单例
     */
    public static Configuration getInstance() {
        return CFG;
    }

    /*
     * 初始化配置文件
     */
    MyConfiguration() {
        props = new Properties();

        InputStream input = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME);

        if (input != null) {
            try {
                props.loadFromXML(input);
            } catch (Exception e) {
                logger.error("MyConfiguration.MyConfiguration error!", e);
            }
        }
    }

    /**
     * 返回useSmart标志位 useSmart =true ，分词器使用智能切分策略， =false则使用细粒度切分
     *
     * @return useSmart
     */
    public boolean useSmart() {
        return useSmart;
    }

    /**
     * 设置useSmart标志位 useSmart =true ，分词器使用智能切分策略， =false则使用细粒度切分
     *
     * @param useSmart
     */
    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    /**
     * 新加函数：设置主词典路径
     *
     * @return String 主词典路径
     */
    public void setMainDictionary(String path) {
        if(!StringUtils.isBlank(path)){
            this.PATH_DIC_MAIN = path;
        }
    }

    /**
     * 获取主词典路径
     *
     * @return String 主词典路径
     */
    public String getMainDictionary() {
        return PATH_DIC_MAIN;
    }

    /**
     * 获取量词词典路径
     *
     * @return String 量词词典路径
     */
    public String getQuantifierDicionary() {
        return PATH_DIC_QUANTIFIER;
    }

    /**
     * 获取扩展字典配置路径
     *
     * @return List<String> 相对类加载器的路径
     */
    public List<String> getExtDictionarys() {
        List<String> extDictFiles = new ArrayList<String>(2);

        String extDictCfg = props.getProperty(EXT_DICT);
        if (extDictCfg != null) {
            // 使用;分割多个扩展字典配置
            String[] filePaths = extDictCfg.split(";");
            if (filePaths != null) {
                for (String filePath : filePaths) {
                    if (filePath != null && !"".equals(filePath.trim())) {
                        extDictFiles.add(filePath.trim());
                    }
                }
            }
        }
        return extDictFiles;
    }

    /**
     * 获取扩展停止词典配置路径
     *
     * @return List<String> 相对类加载器的路径
     */
    public List<String> getExtStopWordDictionarys() {
        List<String> extStopWordDictFiles = new ArrayList<String>(2);
        String extStopWordDictCfg = props.getProperty(EXT_STOP);
        if (extStopWordDictCfg != null) {
            // 使用;分割多个扩展字典配置
            String[] filePaths = extStopWordDictCfg.split(";");
            if (filePaths != null) {
                for (String filePath : filePaths) {
                    if (filePath != null && !"".equals(filePath.trim())) {
                        extStopWordDictFiles.add(filePath.trim());
                    }
                }
            }
        }
        return extStopWordDictFiles;
    }

    public static void main(String[] args) {
        MyConfiguration mycfg = new MyConfiguration();
        mycfg.setUseSmart(true);  //true　用智能分词　，false细粒度
//        mycfg.setMainDictionary("ikanalyzer/model.dic");//动态设置自定义的词典

        String text = "2010款 1.0L 新版实用型短车身";
        StringReader reader = new StringReader(text);
        IKSegmenter ik = new IKSegmenter(reader, mycfg);
        Lexeme lex = null;
        try {
            while((lex=ik.next())!=null){
                System.out.print(lex.getLexemeText()+"|");
            }
        }catch (Exception e) {
        }
    }
}