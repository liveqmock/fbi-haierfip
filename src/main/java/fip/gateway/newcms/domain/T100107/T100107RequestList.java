package fip.gateway.newcms.domain.T100107;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2012-10-10
 * Time: 17:32:16
 * To change this template use File | Settings | File Templates.
 */
public class T100107RequestList {
    @XStreamImplicit(itemFieldName="ROWS")
	private List<T100107RequestRecord> content= new ArrayList();

    public List getContent() {
        return this.content;
    }

    public void add(T100107RequestRecord record) {
        this.content.add(record);
    }

}
