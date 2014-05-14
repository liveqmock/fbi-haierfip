package pub.service.dao;

/**
 * 携带查询所使用的查询条件.
 * User: zhanrui
 * Date: 2010-8-24
 * Time: 17:29:47
 * To change this template use File | Settings | File Templates.
 */

public class Condition {

    //Fields
    private String[] properties;
    private String[] operators;
    private Object[] values;
    private Integer page;
    private Integer size;

    public Condition() {
    }

    public Condition(String[] properties, Object[] values) {
        this.properties = properties;
        this.values = values;
    }

    public Condition(String[] properties, String[] operators, Object[] values) {
        this.properties = properties;
        this.operators = operators;
        this.values = values;
    }

    public String[] getOperators() {
        return operators;
    }

    public void setOperators(String[] operators) {
        this.operators = operators;
    }

    public String[] getProperties() {
        return properties;
    }

    public void setProperties(String[] properties) {
        this.properties = properties;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

}
