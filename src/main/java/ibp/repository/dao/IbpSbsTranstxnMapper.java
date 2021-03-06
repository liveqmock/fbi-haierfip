package ibp.repository.dao;

import ibp.repository.model.IbpSbsTranstxn;
import ibp.repository.model.IbpSbsTranstxnExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface IbpSbsTranstxnMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_TRANSTXN
     *
     * @mbggenerated Wed Nov 05 14:58:40 CST 2014
     */
    int countByExample(IbpSbsTranstxnExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_TRANSTXN
     *
     * @mbggenerated Wed Nov 05 14:58:40 CST 2014
     */
    int deleteByExample(IbpSbsTranstxnExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_TRANSTXN
     *
     * @mbggenerated Wed Nov 05 14:58:40 CST 2014
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_TRANSTXN
     *
     * @mbggenerated Wed Nov 05 14:58:40 CST 2014
     */
    int insert(IbpSbsTranstxn record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_TRANSTXN
     *
     * @mbggenerated Wed Nov 05 14:58:40 CST 2014
     */
    int insertSelective(IbpSbsTranstxn record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_TRANSTXN
     *
     * @mbggenerated Wed Nov 05 14:58:40 CST 2014
     */
    List<IbpSbsTranstxn> selectByExample(IbpSbsTranstxnExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_TRANSTXN
     *
     * @mbggenerated Wed Nov 05 14:58:40 CST 2014
     */
    IbpSbsTranstxn selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_TRANSTXN
     *
     * @mbggenerated Wed Nov 05 14:58:40 CST 2014
     */
    int updateByExampleSelective(@Param("record") IbpSbsTranstxn record, @Param("example") IbpSbsTranstxnExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_TRANSTXN
     *
     * @mbggenerated Wed Nov 05 14:58:40 CST 2014
     */
    int updateByExample(@Param("record") IbpSbsTranstxn record, @Param("example") IbpSbsTranstxnExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_TRANSTXN
     *
     * @mbggenerated Wed Nov 05 14:58:40 CST 2014
     */
    int updateByPrimaryKeySelective(IbpSbsTranstxn record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_TRANSTXN
     *
     * @mbggenerated Wed Nov 05 14:58:40 CST 2014
     */
    int updateByPrimaryKey(IbpSbsTranstxn record);

    @Select("select max(substr(t.serialno, 8)) from IBP_SBS_TRANSTXN t")
    String qryMaxSerialNo();
}