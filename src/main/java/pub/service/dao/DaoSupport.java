package pub.service.dao;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-8-24
 * Time: 17:27:29
 * To change this template use File | Settings | File Templates.
 */

import java.io.Serializable;
import java.util.List;


public interface DaoSupport {

    
    public Serializable insert(Object vo);


    public void update(Object vo);


    public void update(List list);


    public void delete(Object vo);

    /**
     * 根据主键值删除一个值对象
     *
     * @param c 值对象的类
     * @param s 主键值
     */
    public void delete(Class c, Serializable s);


    public void delete(List list);

    /**
     * 根据条件查询单表数据
     *
     * @param selectClause 查询语句中from之前的部分(包括select),如果是(select *),则不写，为null.
     * @param className    值对象的类名
     * @param properties   查询条件的属性名列表
     * @param operators    查询条件的操作符列表，如果查询条件中存在不为<b>=</b>的操作符，需要填写该列表，否则为null
     * @param values       查询条件的值列表，该列表应当与属性列表一一对应
     * @param firstRow     返回查询结果的起始行，如果不需要，则设置为0
     * @param maxRows      返回查询结果的最大行数，如果不需要，则设置为0
     * @return 查询结果的列表
     */
    public List query(String selectClause, String className, String[] properties, String[] operators, Object[] values, int firstRow, int maxRows);

    /**
     * 根据主键值得到一个值对象

     */
    public Object load(Class c, Serializable s);
}
