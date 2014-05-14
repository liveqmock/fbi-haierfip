package pub.service.dao;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-8-24
 * Time: 17:25:51
 * To change this template use File | Settings | File Templates.
 */

import java.io.Serializable;
import java.util.List;


public abstract class BaseDao {

    private DaoSupport daoSupport = null;

    /**
     * @return the daoSupport
     */
    public DaoSupport getDaoSupport() {
        return daoSupport;
    }

    /**
     * @param daoSupport the daoSupport to set
     */
    public void setDaoSupport(DaoSupport daoSupport) {
        this.daoSupport = daoSupport;
    }

    public void insert(Object vo) {
        daoSupport.insert(vo);
    }

    public void update(Object vo) {
        daoSupport.update(vo);
    }

    public void delete(Object vo) {
        daoSupport.delete(vo);
    }

    public void delete(Class c, Serializable s) {
        daoSupport.delete(c, s);
    }

    public void delete(String className, Condition condition) {
        List list = daoSupport.query("", className, condition.getProperties(), condition.getOperators(),
                condition.getValues(), 0, 0);
        daoSupport.delete(list);
    }

    public Object load(Class c, Serializable s) {
        return daoSupport.load(c, s);
    }

    public int getCount(String className, String[] propertyNames, String[] operators, Object[] values) {
        List list = daoSupport.query("select count(*)", className, propertyNames, operators, values, 0, 0);
        return ((Integer) list.get(0)).intValue();
    }

    public int getCount(String className, Condition condition) {
        return this.getCount(className, condition.getProperties(), condition.getOperators(),
                condition.getValues());
    }

    public ResultSet query(String className, String[] propertyNames, String[] operators, Object[] values,
                           int page, int size) {
        List list = daoSupport.query(null, className, propertyNames, operators, values, (page - 1) * size, size);
        int count = 0;
        if (page > 0) {
            count = this.getCount(className, propertyNames, operators, values);
        } else {
            count = list.size();
        }
        return new ResultSet(list, count, page, size);
    }

    public ResultSet query(String className, Condition condition) {
        if (condition == null) {
            return this.query(className, null, null, null, 0, 0);
        }
        return this.query(className, condition.getProperties(), condition.getOperators(), condition.getValues(),
                condition.getPage() == null ? 0 : condition.getPage().intValue(),
                condition.getSize() == null ? 0 : condition.getSize().intValue());
    }
}
