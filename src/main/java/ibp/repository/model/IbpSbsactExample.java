package ibp.repository.model;

import java.util.ArrayList;
import java.util.List;

public class IbpSbsActExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    public IbpSbsActExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andPkidIsNull() {
            addCriterion("PKID is null");
            return (Criteria) this;
        }

        public Criteria andPkidIsNotNull() {
            addCriterion("PKID is not null");
            return (Criteria) this;
        }

        public Criteria andPkidEqualTo(String value) {
            addCriterion("PKID =", value, "pkid");
            return (Criteria) this;
        }

        public Criteria andPkidNotEqualTo(String value) {
            addCriterion("PKID <>", value, "pkid");
            return (Criteria) this;
        }

        public Criteria andPkidGreaterThan(String value) {
            addCriterion("PKID >", value, "pkid");
            return (Criteria) this;
        }

        public Criteria andPkidGreaterThanOrEqualTo(String value) {
            addCriterion("PKID >=", value, "pkid");
            return (Criteria) this;
        }

        public Criteria andPkidLessThan(String value) {
            addCriterion("PKID <", value, "pkid");
            return (Criteria) this;
        }

        public Criteria andPkidLessThanOrEqualTo(String value) {
            addCriterion("PKID <=", value, "pkid");
            return (Criteria) this;
        }

        public Criteria andPkidLike(String value) {
            addCriterion("PKID like", value, "pkid");
            return (Criteria) this;
        }

        public Criteria andPkidNotLike(String value) {
            addCriterion("PKID not like", value, "pkid");
            return (Criteria) this;
        }

        public Criteria andPkidIn(List<String> values) {
            addCriterion("PKID in", values, "pkid");
            return (Criteria) this;
        }

        public Criteria andPkidNotIn(List<String> values) {
            addCriterion("PKID not in", values, "pkid");
            return (Criteria) this;
        }

        public Criteria andPkidBetween(String value1, String value2) {
            addCriterion("PKID between", value1, value2, "pkid");
            return (Criteria) this;
        }

        public Criteria andPkidNotBetween(String value1, String value2) {
            addCriterion("PKID not between", value1, value2, "pkid");
            return (Criteria) this;
        }

        public Criteria andActnumIsNull() {
            addCriterion("ACTNUM is null");
            return (Criteria) this;
        }

        public Criteria andActnumIsNotNull() {
            addCriterion("ACTNUM is not null");
            return (Criteria) this;
        }

        public Criteria andActnumEqualTo(String value) {
            addCriterion("ACTNUM =", value, "actnum");
            return (Criteria) this;
        }

        public Criteria andActnumNotEqualTo(String value) {
            addCriterion("ACTNUM <>", value, "actnum");
            return (Criteria) this;
        }

        public Criteria andActnumGreaterThan(String value) {
            addCriterion("ACTNUM >", value, "actnum");
            return (Criteria) this;
        }

        public Criteria andActnumGreaterThanOrEqualTo(String value) {
            addCriterion("ACTNUM >=", value, "actnum");
            return (Criteria) this;
        }

        public Criteria andActnumLessThan(String value) {
            addCriterion("ACTNUM <", value, "actnum");
            return (Criteria) this;
        }

        public Criteria andActnumLessThanOrEqualTo(String value) {
            addCriterion("ACTNUM <=", value, "actnum");
            return (Criteria) this;
        }

        public Criteria andActnumLike(String value) {
            addCriterion("ACTNUM like", value, "actnum");
            return (Criteria) this;
        }

        public Criteria andActnumNotLike(String value) {
            addCriterion("ACTNUM not like", value, "actnum");
            return (Criteria) this;
        }

        public Criteria andActnumIn(List<String> values) {
            addCriterion("ACTNUM in", values, "actnum");
            return (Criteria) this;
        }

        public Criteria andActnumNotIn(List<String> values) {
            addCriterion("ACTNUM not in", values, "actnum");
            return (Criteria) this;
        }

        public Criteria andActnumBetween(String value1, String value2) {
            addCriterion("ACTNUM between", value1, value2, "actnum");
            return (Criteria) this;
        }

        public Criteria andActnumNotBetween(String value1, String value2) {
            addCriterion("ACTNUM not between", value1, value2, "actnum");
            return (Criteria) this;
        }

        public Criteria andActnamIsNull() {
            addCriterion("ACTNAM is null");
            return (Criteria) this;
        }

        public Criteria andActnamIsNotNull() {
            addCriterion("ACTNAM is not null");
            return (Criteria) this;
        }

        public Criteria andActnamEqualTo(String value) {
            addCriterion("ACTNAM =", value, "actnam");
            return (Criteria) this;
        }

        public Criteria andActnamNotEqualTo(String value) {
            addCriterion("ACTNAM <>", value, "actnam");
            return (Criteria) this;
        }

        public Criteria andActnamGreaterThan(String value) {
            addCriterion("ACTNAM >", value, "actnam");
            return (Criteria) this;
        }

        public Criteria andActnamGreaterThanOrEqualTo(String value) {
            addCriterion("ACTNAM >=", value, "actnam");
            return (Criteria) this;
        }

        public Criteria andActnamLessThan(String value) {
            addCriterion("ACTNAM <", value, "actnam");
            return (Criteria) this;
        }

        public Criteria andActnamLessThanOrEqualTo(String value) {
            addCriterion("ACTNAM <=", value, "actnam");
            return (Criteria) this;
        }

        public Criteria andActnamLike(String value) {
            addCriterion("ACTNAM like", value, "actnam");
            return (Criteria) this;
        }

        public Criteria andActnamNotLike(String value) {
            addCriterion("ACTNAM not like", value, "actnam");
            return (Criteria) this;
        }

        public Criteria andActnamIn(List<String> values) {
            addCriterion("ACTNAM in", values, "actnam");
            return (Criteria) this;
        }

        public Criteria andActnamNotIn(List<String> values) {
            addCriterion("ACTNAM not in", values, "actnam");
            return (Criteria) this;
        }

        public Criteria andActnamBetween(String value1, String value2) {
            addCriterion("ACTNAM between", value1, value2, "actnam");
            return (Criteria) this;
        }

        public Criteria andActnamNotBetween(String value1, String value2) {
            addCriterion("ACTNAM not between", value1, value2, "actnam");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("STATUS is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(String value) {
            addCriterion("STATUS =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(String value) {
            addCriterion("STATUS <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(String value) {
            addCriterion("STATUS >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(String value) {
            addCriterion("STATUS >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(String value) {
            addCriterion("STATUS <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(String value) {
            addCriterion("STATUS <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLike(String value) {
            addCriterion("STATUS like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotLike(String value) {
            addCriterion("STATUS not like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<String> values) {
            addCriterion("STATUS in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<String> values) {
            addCriterion("STATUS not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(String value1, String value2) {
            addCriterion("STATUS between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(String value1, String value2) {
            addCriterion("STATUS not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andOpndatIsNull() {
            addCriterion("OPNDAT is null");
            return (Criteria) this;
        }

        public Criteria andOpndatIsNotNull() {
            addCriterion("OPNDAT is not null");
            return (Criteria) this;
        }

        public Criteria andOpndatEqualTo(String value) {
            addCriterion("OPNDAT =", value, "opndat");
            return (Criteria) this;
        }

        public Criteria andOpndatNotEqualTo(String value) {
            addCriterion("OPNDAT <>", value, "opndat");
            return (Criteria) this;
        }

        public Criteria andOpndatGreaterThan(String value) {
            addCriterion("OPNDAT >", value, "opndat");
            return (Criteria) this;
        }

        public Criteria andOpndatGreaterThanOrEqualTo(String value) {
            addCriterion("OPNDAT >=", value, "opndat");
            return (Criteria) this;
        }

        public Criteria andOpndatLessThan(String value) {
            addCriterion("OPNDAT <", value, "opndat");
            return (Criteria) this;
        }

        public Criteria andOpndatLessThanOrEqualTo(String value) {
            addCriterion("OPNDAT <=", value, "opndat");
            return (Criteria) this;
        }

        public Criteria andOpndatLike(String value) {
            addCriterion("OPNDAT like", value, "opndat");
            return (Criteria) this;
        }

        public Criteria andOpndatNotLike(String value) {
            addCriterion("OPNDAT not like", value, "opndat");
            return (Criteria) this;
        }

        public Criteria andOpndatIn(List<String> values) {
            addCriterion("OPNDAT in", values, "opndat");
            return (Criteria) this;
        }

        public Criteria andOpndatNotIn(List<String> values) {
            addCriterion("OPNDAT not in", values, "opndat");
            return (Criteria) this;
        }

        public Criteria andOpndatBetween(String value1, String value2) {
            addCriterion("OPNDAT between", value1, value2, "opndat");
            return (Criteria) this;
        }

        public Criteria andOpndatNotBetween(String value1, String value2) {
            addCriterion("OPNDAT not between", value1, value2, "opndat");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated do_not_delete_during_merge Thu Nov 13 11:47:25 CST 2014
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table FIP.IBP_SBS_ACT
     *
     * @mbggenerated Thu Nov 13 11:47:25 CST 2014
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}