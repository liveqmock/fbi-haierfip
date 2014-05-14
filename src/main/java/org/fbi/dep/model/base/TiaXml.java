package org.fbi.dep.model.base;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 13-4-3
 * Time: 上午12:13
 * To change this template use File | Settings | File Templates.
 */
public abstract class TiaXml implements Serializable{
     public TiaXml transform(String xml) {
         try {
         return getTia(xml);
         }catch (Exception e) {
             //    MSG_PARSE_FAILED("1100", "报文解析错误"),
             throw new RuntimeException("1100|报文解析错误");
         }
     }

    public abstract TiaXml getTia(String xml);
}
