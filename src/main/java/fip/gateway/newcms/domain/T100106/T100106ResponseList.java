package fip.gateway.newcms.domain.T100106;

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
public class T100106ResponseList {
    @XStreamImplicit(itemFieldName="ROWS")
	private List<T100106ResponseRecord> content= new ArrayList();

    public List getContent() {
        return this.content;
    }

    public void add(T100106ResponseRecord record) {
        this.content.add(record);
    }

}
