package fip.gateway.sbs.core;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-14
 * Time: 下午5:15
 * To change this template use File | Settings | File Templates.
 */
public class SBSResponse4MultiRecord extends SBSResponse{
    private static Logger logger = LoggerFactory.getLogger(SBSResponse4MultiRecord.class);

//    private byte[] buffer;
//
//    private String formcode;
//
//    private SOFHeader sofHeader = new SOFHeader();
    private SOFDataHeader4MultiRecord sofDataHeader = new SOFDataHeader4MultiRecord();
    private SOFDataDetail sofDataDetail;
    private List<SOFDataDetail> sofDataDetailList = new ArrayList<SOFDataDetail>();

//    public byte[] getBuffer() {
//        return buffer;
//    }

//    public String getFormcode() {
//        return formcode;
//    }

    public SOFDataHeader4MultiRecord getSofDataHeader() {
        return sofDataHeader;
    }

    public void setSofDataHeader(SOFDataHeader4MultiRecord sofDataHeader) {
        this.sofDataHeader = sofDataHeader;
    }

    public void setSofDataDetail(SOFDataDetail sofDataDetail) {
        this.sofDataDetail = sofDataDetail;
    }

    public List<SOFDataDetail> getSofDataDetailList() {
        return sofDataDetailList;
    }

//    public void setSofHeader(SOFHeader sofHeader) {
//        this.sofHeader = sofHeader;
//    }

//==========================================================

    public void init(byte[] buffer){

        try {
            this.buffer = buffer;
            this.sofHeader.assembleFields(this.buffer);
            this.formcode = this.sofHeader.getFormCode();

            //SOFDATA处理 (判断form号？ 判断SOFDATA长度？)
            if (formcode.substring(0, 1).equals("T")) {
                this.sofDataHeader.assembleFields(this.buffer);

                int recordLength = this.sofDataDetail.getMessageLength();
                int recordOffset = this.sofHeader.getMessageLength() + this.sofDataHeader.getMessageLength();
                int recordCount = Integer.parseInt(this.sofDataHeader.getCurcnt());

                int srcPos = recordOffset;
                String className = this.sofDataDetail.getClass().getName();

                for (int i = 0; i < recordCount; i++) {
                    byte[] recordBuffer = new byte[recordLength];
                    System.arraycopy(buffer, srcPos, recordBuffer, 0, recordBuffer.length);
                    //TODO  this.sofDataDetail 需要清零?
                    this.sofDataDetail.assembleFields(recordBuffer);
                    SOFDataDetail sofDataDetail = (SOFDataDetail) Class.forName(className).newInstance();
                    BeanUtils.copyProperties(sofDataDetail, this.sofDataDetail);
                    this.sofDataDetailList.add(sofDataDetail);
                    srcPos += recordLength;
                }
            }
        } catch (Exception e) {
            logger.error("对SBS报文处理时出现错误。", e);
            throw new RuntimeException("对SBS报文处理时出现错误。", e);
        }

    }

    //============================================================
}
