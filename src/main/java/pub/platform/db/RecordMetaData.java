/**
 * Copyright 2003 ZhongTian, Inc. All rights reserved.
 *
 * qingdao tec PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * $Id: RecordMetaData.java,v 1.1 2006/05/17 09:19:31 wiserd Exp $
 * File:RecordMetaData.java
 * Date Author Changes
 * March 5 2003 wangdeliang Created
 */

package pub.platform.db;

/**
 * 字段的定义类
 *
 * @author <a href="mailto:wuyeyuan@tom.com">wuyeyuan</a>
 * @version $Revision: 1.1 $ $Date: 2006/05/17 09:19:31 $
 */

public class RecordMetaData {
    private String name;
    private String typeName;
    private int type;
    private String caption;
    private int seqno;

    /**
     *
     * @param name 字段的名字
     * @param typeName 字段类型的名字
     * @param type 字段的类型
     * @param caption 字段标题/注释
     * @roseuid 3E5D81390135
     */
    public RecordMetaData(String name, String typeName, String caption,int seqno) {
        this.name     = name;
        this.typeName = typeName;
        this.seqno    = seqno;

        if ( typeName.equals(MetaDataTypes.STRING) )
            this.type = MetaDataTypes.STRING_TP;
        else if ( typeName.equals(MetaDataTypes.BIGDECIMAL) )
            this.type = MetaDataTypes.BIGDECIMAL_TP;
        else if ( typeName.equals(MetaDataTypes.CALENDAR) )
            this.type = MetaDataTypes.CALENDAR_TP;
        else if ( typeName.equals(MetaDataTypes.BIGINTEGER) )
            this.type = MetaDataTypes.BIGINTEGER_TP;
        else if ( typeName.equals(MetaDataTypes.BOOLEAN) )
            this.type = MetaDataTypes.BOOLEAN_TP;
        else if ( typeName.equals(MetaDataTypes.BYTE) )
            this.type = MetaDataTypes.BYTE_TP;
        else if ( typeName.equals(MetaDataTypes.CHARACTER) )
            this.type = MetaDataTypes.CHARACTER_TP;
        else if ( typeName.equals(MetaDataTypes.DOUBLE) )
            this.type = MetaDataTypes.DOUBLE_TP;
        else if ( typeName.equals(MetaDataTypes.FLOAT) )
            this.type = MetaDataTypes.FLOAT_TP;
        else if ( typeName.equals(MetaDataTypes.INTEGER) )
            this.type = MetaDataTypes.INTEGER_TP;
        else if ( typeName.equals(MetaDataTypes.LONG) )
            this.type = MetaDataTypes.LONG_TP;
        else if ( typeName.equals(MetaDataTypes.SHORT) )
            this.type = MetaDataTypes.SHORT_TP;
        else if ( typeName.equals(MetaDataTypes.TIMESTAMP) )
            this.type = MetaDataTypes.TIMESTAMP_TP;
        else
            this.type = -1;
        this.caption  = caption;
    }

    /**
     * @roseuid 3E5D8128039E
     */
    public RecordMetaData() {

    }

    /**
     * @return String
     * @roseuid 3E5D816A02F8
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * @roseuid 3E5D81760223
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return String
     * @roseuid 3E5D81870372
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName
     * @roseuid 3E5D8192036E
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return int
     * @roseuid 3E5D819F018C
     */
    public int getType() {
        return type;
    }

    /**
     * @param type
     * @roseuid 3E5D81AC00EB
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return String
     * @roseuid 3E5D81B700A0
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption
     * @roseuid 3E5D81BF037D
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
    public void setSeqno(int seqno) {
        this.seqno = seqno;
    }
    public int getSeqno() {
        return seqno;
    }
}
