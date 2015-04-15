package fip.gateway.newcms.domain.T100109;

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
public class T100109ResponseList {
    @XStreamImplicit(itemFieldName="ROWS")
	private List<T100109ResponseRecord> content= new ArrayList();

    public List getContent() {
        return this.content;
    }

    public void add(T100109ResponseRecord record) {
        this.content.add(record);
    }

}
