package ibp.repository.dao;

import ibp.repository.model.IbpSbsAct;
import ibp.repository.model.IbpSbsActExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface IbpSbsActMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    int countByExample(IbpSbsActExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    int deleteByExample(IbpSbsActExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    int insert(IbpSbsAct record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    int insertSelective(IbpSbsAct record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    List<IbpSbsAct> selectByExample(IbpSbsActExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    int updateByExampleSelective(@Param("record") IbpSbsAct record, @Param("example") IbpSbsActExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    int updateByExample(@Param("record") IbpSbsAct record, @Param("example") IbpSbsActExample example);

    @Select(" select pkid, actnum, actnam, status, opndat from IBP_SBS_ACT t " +
            " where t.actnam like #{act} and " +
            " ( t.ACTNUM like '%2017001' or t.ACTNUM like '%2033001' or t.ACTNUM like '%2301001')")
    List<IbpSbsAct> qrySbsActToTrans(@Param("act") String act);
}