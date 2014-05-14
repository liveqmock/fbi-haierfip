package fip.gateway.sbs.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-14
 * Time: 下午5:15
 * To change this template use File | Settings | File Templates.
 */
public class SBSResponse4SingleRecord extends SBSResponse {
    private static Logger logger = LoggerFactory.getLogger(SBSResponse4SingleRecord.class);

//    private byte[] buffer;
//
//    private String formcode;
//
//    private SOFHeader sofHeader = new SOFHeader();
    private SOFDataHeader4SingleRecord sofDataHeader = new SOFDataHeader4SingleRecord();
    private SOFDataDetail sofDataDetail;

//    public byte[] getBuffer() {
//        return buffer;
//    }
//
//    public String getFormcode() {
//        return formcode;
//    }

    public SOFDataHeader4SingleRecord getSofDataHeader() {
        return sofDataHeader;
    }

    public void setSofDataHeader(SOFDataHeader4SingleRecord sofDataHeader) {
        this.sofDataHeader = sofDataHeader;
    }

    public void setSofDataDetail(SOFDataDetail sofDataDetail) {
        this.sofDataDetail = sofDataDetail;
    }

//    public void setSofHeader(SOFHeader sofHeader) {
//        this.sofHeader = sofHeader;
//    }

    public SOFDataDetail getSofDataDetail() {
        return sofDataDetail;
    }
//==========================================================

    public void init(byte[] buffer) {

        try {
            this.buffer = buffer;
            this.sofHeader.assembleFields(this.buffer);
            this.formcode = this.sofHeader.getFormCode();

            this.sofDataHeader.assembleFields(this.buffer);

            int recordLength = this.sofDataDetail.getMessageLength();
            int recordOffset = this.sofHeader.getMessageLength() + this.sofDataHeader.getMessageLength();

            //SOFDATA处理 (判断form号？ 判断SOFDATA长度？)
            if (formcode.substring(0, 1).equals("T")) {
                byte[] recordBuffer = new byte[recordLength];
                System.arraycopy(buffer, recordOffset, recordBuffer, 0, recordBuffer.length);
                //TODO  this.sofDataDetail 需要清零?
                this.sofDataDetail.assembleFields(recordBuffer);
            }else{
                byte[] recordBuffer = new byte[this.sofDataHeader.getDatalen()];
                System.arraycopy(buffer, recordOffset, recordBuffer, 0, recordBuffer.length);
                this.forminfo = new String(recordBuffer);
            }
        } catch (Exception e) {
            logger.error("对SBS报文处理时出现错误。", e);
            throw new RuntimeException("对SBS报文处理时出现错误。", e);
        }

    }

    //============================================================
}
