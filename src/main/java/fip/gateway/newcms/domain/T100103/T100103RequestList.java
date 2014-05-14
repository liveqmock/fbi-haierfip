package fip.gateway.newcms.domain.T100103;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-10-10
 * Time: 17:32:16
 * To change this template use File | Settings | File Templates.
 */
public class T100103RequestList {
    @XStreamImplicit(itemFieldName="ROWS")
	private List<T100103RequestRecord> content= new ArrayList();

    public List getContent() {
        return this.content;
    }

    public void add(T100103RequestRecord record) {
        this.content.add(record);
    }

}
