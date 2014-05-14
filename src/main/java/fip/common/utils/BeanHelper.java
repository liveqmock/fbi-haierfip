package fip.common.utils;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2011-1-10
 * Time: 16:44:03
 * To change this template use File | Settings | File Templates.
 */
public class BeanHelper {

    /**
     * 只copy 完全相同的属性（提供两种Date的转换）
     * @param orig
     * @param dest
     * @return
     */
    public static Object copy(Object dest, Object orig) {
        PropertyDescriptor[] origPds = PropertyUtils.getPropertyDescriptors(orig);
        PropertyDescriptor[] destPds = PropertyUtils.getPropertyDescriptors(dest);
        String[] propertys = new String[200];
        int t = 0;
        for (int o = 0; o < origPds.length; o++) {
            PropertyDescriptor origPd = origPds[o];
            String origDispName = origPd.getDisplayName();
            String origTypeName = origPd.getPropertyType().getName();
            for (int d = 0; d < destPds.length; d++) {
                PropertyDescriptor destPd = destPds[d];
                //if ((origPd.getDisplayName().equals(destPd.getDisplayName())) && (!destPd.getDisplayName().equals("class"))) {
                if ((origDispName.equals(destPd.getDisplayName()))
                        && (origTypeName.equals(destPd.getPropertyType().getName()))
                        && (!destPd.getDisplayName().equals("class"))) {
                    propertys[t] = origPd.getDisplayName();
                    t = t + 1;
                    break;
                }
            }
        }

        try {
            for (int i = 0; i < propertys.length; i++) {
                if (propertys[i] == null)
                    return dest;

                Object srcDate = PropertyUtils.getNestedProperty(orig, propertys[i]);

                if (srcDate instanceof java.util.Date) {
                    srcDate = new java.sql.Date(((java.util.Date) srcDate).getTime());
                }

                PropertyUtils.setNestedProperty(dest, propertys[i], srcDate);
            }
        } catch (Exception e) {
            throw new RuntimeException("Bean Copy Error!");
        }

        return dest;
    }


}
