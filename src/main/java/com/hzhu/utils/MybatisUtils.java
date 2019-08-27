package com.hzhu.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description TODO
 * @Date 2019/8/27 13:55
 * @Created by CZB
 */
public class MybatisUtils {

    private static final String MYBATIS_FILE = "mybatis/mybatis-config.xml";

    private SqlSessionFactory sqlSessionFactory;

    private static Logger logger = LoggerFactory.getLogger(MybatisUtils.class);

    public MybatisUtils() {

        initSqlSessionFactory();

    }

    public SqlSessionFactory initSqlSessionFactory() {
        try {
            InputStream input = Resources.getResourceAsStream(MYBATIS_FILE);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(input);
        } catch (IOException e) {
            logger.error("MybatisUtils.initSqlSessionFactory error!", e);
        }
        return sqlSessionFactory;
    }

    public SqlSession getSqlSession() {
        return this.sqlSessionFactory.openSession();
    }

}
