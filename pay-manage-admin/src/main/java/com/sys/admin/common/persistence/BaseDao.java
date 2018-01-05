package com.sys.admin.common.persistence;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Sort;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.search.FullTextSession;
import org.hibernate.transform.ResultTransformer;

/**
 * DAO支持接口
 */
public interface BaseDao<T> {

    /**
     * 获取实体工厂管理对象
     */
    EntityManager getEntityManager();

    /**
     * 获取 Session
     */
    Session getSession();

    /**
     * 强制与数据库同步
     */
    void flush();

    /**
     * 清除缓存数据
     */
    void clear();

    // -------------- QL Query --------------

    /**
     * QL 分页查询
     * @param page      分页对象
     * @param qlString  HQL字符串
     * @param parameter 可变参数
     * @return 分页对象
     */
    <E> Page<E> find(Page<E> page, String qlString, Object... parameter);

    /**
     * QL 查询
     *
     * @param qlString  HQL字符串
     * @param parameter 可变参数
     * @return 对象列表
     */
    <E> List<E> find(String qlString, Object... parameter);

    /**
     * QL 更新
     *
     * @param qlString  HQL字符串
     * @param parameter 可变参数
     * @return 更新数量
     */
    int update(String qlString, Object... parameter);

    /**
     * 创建 QL 查询对象
     *
     * @param qlString  HQL字符串
     * @param parameter 可变参数
     * @return HQL查询对象
     */
    Query createQuery(String qlString, Object... parameter);

    // -------------- SQL Query --------------

    /**
     * SQL 分页查询
     *
     * @param page        分页对象
     * @param sqlString   SQL字符串
     * @param parameter   可变参数
     * @return 分页对象
     */
    <E> Page<E> findBySql(Page<E> page, String sqlString, Object... parameter);

    /**
     * SQL 分页查询
     *
     * @param page        分页对象
     * @param sqlString   SQL字符串
     * @param resultClass 结果类型
     * @param parameter   可变参数
     * @return 分页对象
     */
    <E> Page<E> findBySql(Page<E> page, String sqlString, Class<?> resultClass, Object... parameter);

    /**
     * SQL 查询
     *
     * @param sqlString SQL字符串
     * @param parameter 可变参数
     * @return 对象列表
     */
    <E> List<E> findBySql(String sqlString, Object... parameter);

    /**
     * SQL 查询
     *
     * @param sqlString   SQL字符串
     * @param resultClass 结果类型
     * @param parameter   可变参数
     * @return 对象列表
     */
    <E> List<E> findBySql(String sqlString, Class<?> resultClass, Object... parameter);

    /**
     * SQL 更新
     *
     * @param sqlString SQL字符串
     * @param parameter 可变参数
     * @return 更新数量
     */
    int updateBySql(String sqlString, Object... parameter);

    /**
     * 创建 SQL 查询对象
     *
     * @param sqlString SQL字符串
     * @param parameter 可变参数
     * @return SQL查询对象
     */
    Query createSqlQuery(String sqlString, Object... parameter);

    // -------------- Criteria --------------

    /**
     * 分页查询
     *
     * @param page 分页对象
     * @return 分页对象
     */
    Page<T> find(Page<T> page);

    /**
     * 使用检索标准对象分页查询
     *
     * @param page             分页对象
     * @param detachedCriteria 检索标准对象
     * @return 分页对象
     */
    Page<T> find(Page<T> page, DetachedCriteria detachedCriteria);

    /**
     * 使用检索标准对象分页查询
     *
     * @param page              分页对象
     * @param detachedCriteria  检索标准对象
     * @param resultTransformer 查询结果转换器
     * @return 分页对象列表
     */
    Page<T> find(Page<T> page, DetachedCriteria detachedCriteria, ResultTransformer resultTransformer);

    /**
     * 使用检索标准对象查询
     *
     * @param detachedCriteria 检索标准对象
     * @return 对象列表
     */
    List<T> find(DetachedCriteria detachedCriteria);

    /**
     * 使用检索标准对象查询
     *
     * @param detachedCriteria  检索标准对象
     * @param resultTransformer 查询结果转换器
     * @return 对象列表
     */
    List<T> find(DetachedCriteria detachedCriteria, ResultTransformer resultTransformer);

    /**
     * 使用检索标准对象查询记录数
     *
     * @param detachedCriteria 检索标准对象
     * @return 记录数
     */
    long count(DetachedCriteria detachedCriteria);

    /**
     * 创建与会话无关的检索标准对象
     * @param criterions Restrictions.eq("name", value);
     * @return 检索标准对象
     */
    DetachedCriteria createDetachedCriteria(Criterion... criterions);

    // -------------- Hibernate search --------------

    /**
     * 获取全文Session
     */
    FullTextSession getFullTextSession();

    /**
     * 建立索引
     */
    void createIndex();

    /**
     * 全文检索
     * @param page 分页对象
     * @param query 关键字查询对象
     * @param queryFilter 查询过滤对象
     * @param sort 排序对象
     * @return 分页对象
     */
    Page<T> search(Page<T> page, BooleanQuery query, BooleanQuery queryFilter, Sort sort);

    /**
     * 获取全文查询对象
     */
    BooleanQuery getFullTextQuery(BooleanClause... booleanClauses);

    /**
     * 获取全文查询对象
     * @param q 查询关键字
     * @param fields 查询字段
     * @return 全文查询对象
     */
    BooleanQuery getFullTextQuery(String q, String... fields);

    /**
     * 设置关键字高亮
     * @param query 查询对象
     * @param list 设置高亮的内容列表
     * @param subLength 截取长度
     * @param fields 字段名
     */
    List<T> keywordsHighlight(BooleanQuery query, List<T> list, int subLength, String... fields);

    /**
     * 设置关键字高亮
     * @param eqMap 相等字段
     * @param neMap 不相等字段
     */
    boolean isExisted(Map<String, Object> eqMap, Map<String, Object> neMap);
}