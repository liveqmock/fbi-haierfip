package fip.common;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-11-1
 * Time: 11:05:24
 * To change this template use File | Settings | File Templates.
 */
public enum IbatisFactory {
    ORACLE;
    private   SqlSessionFactory sessionFactory;

    IbatisFactory(){
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("ibatisConfig.xml");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IBATIS 参数文件读取错误。",e);
        }
        sessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    public   SqlSessionFactory getInstance(){
        return  sessionFactory;
    }
}
