package fip.common;

import org.apache.ibatis.session.SqlSessionFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-11-2
 * Time: 11:55:18
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@SessionScoped
//@ApplicationScoped
public class IbatisManager implements Serializable {
    
    private SqlSessionFactory sessionFactory;

    public IbatisManager(){
         sessionFactory = IbatisFactory.ORACLE.getInstance();
    }

    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
