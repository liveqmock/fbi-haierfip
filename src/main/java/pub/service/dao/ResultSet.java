package pub.service.dao;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-8-24
 * Time: 17:31:22
 * To change this template use File | Settings | File Templates.
 */

import java.io.Serializable;
import java.util.Collection;

/**
 * The result of query
 */
public class ResultSet implements Serializable {
    private static final long serialVersionUID = 1375187828856315635L;
    private Collection collection; //数据集
    private int totalRows;         //总共记录数
    private int totalPages;        //总共页数
    private int page;              //当前页数
    private int pageSize;          //每页记录数

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }


    public Collection getCollection() {
        return collection;
    }


    public void setCollection(Collection collection) {
        this.collection = collection;
    }


    public int getPage() {
        return page;
    }


    public void setPage(int page) {
        this.page = page;
    }


    public int getPageSize() {
        return pageSize;
    }


    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


    public int getTotalRows() {
        return totalRows;
    }


    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public ResultSet(Collection co, int totalRows, int page, int pageSize) {
        if (pageSize == 0) {
            pageSize = totalRows;
        }
        this.setCollection(co);
        this.setTotalRows(totalRows);
        this.setTotalPages(totalRows / pageSize);
        this.setPage(page);
        this.setPageSize(pageSize);
    }

    public ResultSet(Collection co) {
        this.setCollection(co);
        this.setTotalRows(co.size());
        this.setTotalPages(1);
        this.setPage(1);
        this.setPageSize(co.size());
    }
}
