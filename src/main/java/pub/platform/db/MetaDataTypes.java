/**
 * Copyright 2003 ZhongTian, Inc. All rights reserved.
 *
 * qingdao tec PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * $Id: MetaDataTypes.java,v 1.1 2006/05/17 09:19:30 wiserd Exp $
 * File:MetaDataTypes.java
 * Date Author Changes
 * March 5 2003 wangdeliang Created
 */

package pub.platform.db;

/**
 * 定义数据类型
 *
 * @author <a href="mailto:wuyeyuan@tom.com">wuyeyuan</a>
 * @version $Revision: 1.1 $ $Date: 2006/05/17 09:19:30 $
 */

public interface MetaDataTypes {
   int    BOOLEAN_TP    = 10001;
   String BOOLEAN       = "java.lang.Boolean";
   int    BYTE_TP       = 10002;
   String BYTE          = "java.lang.Byte";
   int    SHORT_TP      = 10003;
   String SHORT         = "java.lang.Short";
   int    INTEGER_TP    = 10004;
   String INTEGER       = "java.lang.Integer";
   int    LONG_TP       = 10005;
   String LONG          = "java.lang.Long";
   int    FLOAT_TP      = 10006;
   String FLOAT         = "java.lang.Float";
   int    DOUBLE_TP     = 10007;
   String DOUBLE        = "java.lang.Double";
   int    STRING_TP     = 10008;
   String STRING        = "java.lang.String";
   int    CHARACTER_TP  = 10009;
   String CHARACTER     = "java.lang.Charater";
   int    CALENDAR_TP   = 10010;
   String CALENDAR      = "java.sql.Date";
   int    BIGDECIMAL_TP = 10011;
   String BIGDECIMAL    = "java.math.BigDecimal";
   int    BIGINTEGER_TP = 10012;
   String BIGINTEGER    = "java.math.BigInteger";
   int    TIMESTAMP_TP  = 10013;
   String TIMESTAMP     = "java.sql.Timestamp";

}
