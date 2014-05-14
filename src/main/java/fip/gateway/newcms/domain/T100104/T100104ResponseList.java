package fip.gateway.newcms.domain.T100104;

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
public class T100104ResponseList {
    @XStreamImplicit(itemFieldName="ROWS")
	private List<T100104ResponseRecord> content= new ArrayList();

    public List getContent() {
        return this.content;
    }

    public void add(T100104ResponseRecord record) {
        this.content.add(record);
    }

}
