package fip.repository.dao;

import fip.repository.model.DepSbsZfqs;
import fip.repository.model.DepSbsZfqsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DepSbsZfqsMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.DEP_SBS_ZFQS
     *
     * @mbggenerated Mon Apr 01 15:56:33 CST 2013
     */
    int countByExample(DepSbsZfqsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.DEP_SBS_ZFQS
     *
     * @mbggenerated Mon Apr 01 15:56:33 CST 2013
     */
    int deleteByExample(DepSbsZfqsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.DEP_SBS_ZFQS
     *
     * @mbggenerated Mon Apr 01 15:56:33 CST 2013
     */
    int deleteByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.DEP_SBS_ZFQS
     *
     * @mbggenerated Mon Apr 01 15:56:33 CST 2013
     */
    int insert(DepSbsZfqs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.DEP_SBS_ZFQS
     *
     * @mbggenerated Mon Apr 01 15:56:33 CST 2013
     */
    int insertSelective(DepSbsZfqs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.DEP_SBS_ZFQS
     *
     * @mbggenerated Mon Apr 01 15:56:33 CST 2013
     */
    List<DepSbsZfqs> selectByExample(DepSbsZfqsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.DEP_SBS_ZFQS
     *
     * @mbggenerated Mon Apr 01 15:56:33 CST 2013
     */
    DepSbsZfqs selectByPrimaryKey(String pkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.DEP_SBS_ZFQS
     *
     * @mbggenerated Mon Apr 01 15:56:33 CST 2013
     */
    int updateByExampleSelective(@Param("record") DepSbsZfqs record, @Param("example") DepSbsZfqsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.DEP_SBS_ZFQS
     *
     * @mbggenerated Mon Apr 01 15:56:33 CST 2013
     */
    int updateByExample(@Param("record") DepSbsZfqs record, @Param("example") DepSbsZfqsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.DEP_SBS_ZFQS
     *
     * @mbggenerated Mon Apr 01 15:56:33 CST 2013
     */
    int updateByPrimaryKeySelective(DepSbsZfqs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.DEP_SBS_ZFQS
     *
     * @mbggenerated Mon Apr 01 15:56:33 CST 2013
     */
    int updateByPrimaryKey(DepSbsZfqs record);
}