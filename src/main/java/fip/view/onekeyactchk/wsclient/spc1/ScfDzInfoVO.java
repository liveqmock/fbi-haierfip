/**
 * ScfDzInfoVO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fip.view.onekeyactchk.wsclient.spc1;

public class ScfDzInfoVO  implements java.io.Serializable {
    private java.lang.String action;

    private java.lang.String channel;

    private java.lang.String chnCode;

    private java.lang.String remark;

    private java.lang.String reqSn;

    private java.lang.String rtnCode;

    private java.lang.String rtnMsg;

    private java.lang.String txnCode;

    private java.lang.String txnDate;

    private java.lang.String txnTime;

    private java.lang.String version;

    public ScfDzInfoVO() {
    }

    public ScfDzInfoVO(
           java.lang.String action,
           java.lang.String channel,
           java.lang.String chnCode,
           java.lang.String remark,
           java.lang.String reqSn,
           java.lang.String rtnCode,
           java.lang.String rtnMsg,
           java.lang.String txnCode,
           java.lang.String txnDate,
           java.lang.String txnTime,
           java.lang.String version) {
           this.action = action;
           this.channel = channel;
           this.chnCode = chnCode;
           this.remark = remark;
           this.reqSn = reqSn;
           this.rtnCode = rtnCode;
           this.rtnMsg = rtnMsg;
           this.txnCode = txnCode;
           this.txnDate = txnDate;
           this.txnTime = txnTime;
           this.version = version;
    }


    /**
     * Gets the action value for this ScfDzInfoVO.
     *
     * @return action
     */
    public java.lang.String getAction() {
        return action;
    }


    /**
     * Sets the action value for this ScfDzInfoVO.
     *
     * @param action
     */
    public void setAction(java.lang.String action) {
        this.action = action;
    }


    /**
     * Gets the channel value for this ScfDzInfoVO.
     *
     * @return channel
     */
    public java.lang.String getChannel() {
        return channel;
    }


    /**
     * Sets the channel value for this ScfDzInfoVO.
     *
     * @param channel
     */
    public void setChannel(java.lang.String channel) {
        this.channel = channel;
    }


    /**
     * Gets the chnCode value for this ScfDzInfoVO.
     *
     * @return chnCode
     */
    public java.lang.String getChnCode() {
        return chnCode;
    }


    /**
     * Sets the chnCode value for this ScfDzInfoVO.
     *
     * @param chnCode
     */
    public void setChnCode(java.lang.String chnCode) {
        this.chnCode = chnCode;
    }


    /**
     * Gets the remark value for this ScfDzInfoVO.
     *
     * @return remark
     */
    public java.lang.String getRemark() {
        return remark;
    }


    /**
     * Sets the remark value for this ScfDzInfoVO.
     *
     * @param remark
     */
    public void setRemark(java.lang.String remark) {
        this.remark = remark;
    }


    /**
     * Gets the reqSn value for this ScfDzInfoVO.
     *
     * @return reqSn
     */
    public java.lang.String getReqSn() {
        return reqSn;
    }


    /**
     * Sets the reqSn value for this ScfDzInfoVO.
     *
     * @param reqSn
     */
    public void setReqSn(java.lang.String reqSn) {
        this.reqSn = reqSn;
    }


    /**
     * Gets the rtnCode value for this ScfDzInfoVO.
     *
     * @return rtnCode
     */
    public java.lang.String getRtnCode() {
        return rtnCode;
    }


    /**
     * Sets the rtnCode value for this ScfDzInfoVO.
     *
     * @param rtnCode
     */
    public void setRtnCode(java.lang.String rtnCode) {
        this.rtnCode = rtnCode;
    }


    /**
     * Gets the rtnMsg value for this ScfDzInfoVO.
     *
     * @return rtnMsg
     */
    public java.lang.String getRtnMsg() {
        return rtnMsg;
    }


    /**
     * Sets the rtnMsg value for this ScfDzInfoVO.
     *
     * @param rtnMsg
     */
    public void setRtnMsg(java.lang.String rtnMsg) {
        this.rtnMsg = rtnMsg;
    }


    /**
     * Gets the txnCode value for this ScfDzInfoVO.
     *
     * @return txnCode
     */
    public java.lang.String getTxnCode() {
        return txnCode;
    }


    /**
     * Sets the txnCode value for this ScfDzInfoVO.
     *
     * @param txnCode
     */
    public void setTxnCode(java.lang.String txnCode) {
        this.txnCode = txnCode;
    }


    /**
     * Gets the txnDate value for this ScfDzInfoVO.
     *
     * @return txnDate
     */
    public java.lang.String getTxnDate() {
        return txnDate;
    }


    /**
     * Sets the txnDate value for this ScfDzInfoVO.
     *
     * @param txnDate
     */
    public void setTxnDate(java.lang.String txnDate) {
        this.txnDate = txnDate;
    }


    /**
     * Gets the txnTime value for this ScfDzInfoVO.
     *
     * @return txnTime
     */
    public java.lang.String getTxnTime() {
        return txnTime;
    }


    /**
     * Sets the txnTime value for this ScfDzInfoVO.
     *
     * @param txnTime
     */
    public void setTxnTime(java.lang.String txnTime) {
        this.txnTime = txnTime;
    }


    /**
     * Gets the version value for this ScfDzInfoVO.
     *
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this ScfDzInfoVO.
     *
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ScfDzInfoVO)) return false;
        ScfDzInfoVO other = (ScfDzInfoVO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.action==null && other.getAction()==null) ||
             (this.action!=null &&
              this.action.equals(other.getAction()))) &&
            ((this.channel==null && other.getChannel()==null) ||
             (this.channel!=null &&
              this.channel.equals(other.getChannel()))) &&
            ((this.chnCode==null && other.getChnCode()==null) ||
             (this.chnCode!=null &&
              this.chnCode.equals(other.getChnCode()))) &&
            ((this.remark==null && other.getRemark()==null) ||
             (this.remark!=null &&
              this.remark.equals(other.getRemark()))) &&
            ((this.reqSn==null && other.getReqSn()==null) ||
             (this.reqSn!=null &&
              this.reqSn.equals(other.getReqSn()))) &&
            ((this.rtnCode==null && other.getRtnCode()==null) ||
             (this.rtnCode!=null &&
              this.rtnCode.equals(other.getRtnCode()))) &&
            ((this.rtnMsg==null && other.getRtnMsg()==null) ||
             (this.rtnMsg!=null &&
              this.rtnMsg.equals(other.getRtnMsg()))) &&
            ((this.txnCode==null && other.getTxnCode()==null) ||
             (this.txnCode!=null &&
              this.txnCode.equals(other.getTxnCode()))) &&
            ((this.txnDate==null && other.getTxnDate()==null) ||
             (this.txnDate!=null &&
              this.txnDate.equals(other.getTxnDate()))) &&
            ((this.txnTime==null && other.getTxnTime()==null) ||
             (this.txnTime!=null &&
              this.txnTime.equals(other.getTxnTime()))) &&
            ((this.version==null && other.getVersion()==null) ||
             (this.version!=null &&
              this.version.equals(other.getVersion())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAction() != null) {
            _hashCode += getAction().hashCode();
        }
        if (getChannel() != null) {
            _hashCode += getChannel().hashCode();
        }
        if (getChnCode() != null) {
            _hashCode += getChnCode().hashCode();
        }
        if (getRemark() != null) {
            _hashCode += getRemark().hashCode();
        }
        if (getReqSn() != null) {
            _hashCode += getReqSn().hashCode();
        }
        if (getRtnCode() != null) {
            _hashCode += getRtnCode().hashCode();
        }
        if (getRtnMsg() != null) {
            _hashCode += getRtnMsg().hashCode();
        }
        if (getTxnCode() != null) {
            _hashCode += getTxnCode().hashCode();
        }
        if (getTxnDate() != null) {
            _hashCode += getTxnDate().hashCode();
        }
        if (getTxnTime() != null) {
            _hashCode += getTxnTime().hashCode();
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ScfDzInfoVO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:BeanService", "ScfDzInfoVO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("action");
        elemField.setXmlName(new javax.xml.namespace.QName("", "action"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("channel");
        elemField.setXmlName(new javax.xml.namespace.QName("", "channel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chnCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "chnCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("remark");
        elemField.setXmlName(new javax.xml.namespace.QName("", "remark"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reqSn");
        elemField.setXmlName(new javax.xml.namespace.QName("", "reqSn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rtnCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rtnCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rtnMsg");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rtnMsg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txnCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "txnCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txnDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "txnDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txnTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "txnTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("version");
        elemField.setXmlName(new javax.xml.namespace.QName("", "version"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType,
           java.lang.Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType,
           java.lang.Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
