package mybatis.plugins.util;

import mybatis.plugins.main.PageHelper;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutorUtil {
    private static final String MS_COUNT_SUFFIX = "_COUNT";
    private static final String MS_COUNT_Infix = " COUNT(0) ";
    private static final String FIRST_PAGEHELPER = "first_pageHelper";
    private static final String SECOND_PAGEHELPER = "second_pageHelper";

    public static int count(Executor executor, MappedStatement ms, Object parameterObject, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        //1、创建一个新的MappenStatement
        String msId = ms.getId() + MS_COUNT_SUFFIX;

        MappedStatement.Builder countMsBuilder = new MappedStatement.Builder(ms.getConfiguration(), msId, ms.getSqlSource(), ms.getSqlCommandType());

        countMsBuilder.cache(ms.getCache());
        countMsBuilder.fetchSize(ms.getFetchSize());
        countMsBuilder.databaseId(ms.getDatabaseId());
        countMsBuilder.flushCacheRequired(ms.isFlushCacheRequired());
        countMsBuilder.parameterMap(ms.getParameterMap());
//        countMsBuilder.keyColumn(String.join(",", ms.getKeyColumns()));//这个是不需要的， 因为这个参数只对insert 和 update 生效。
        countMsBuilder.keyGenerator(ms.getKeyGenerator());
        countMsBuilder.resource(ms.getResource());

        //重点，resultmap 要改变，因为返回结果已经变了
        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), int.class, new ArrayList<>()).build();
        resultMaps.add(resultMap);
        countMsBuilder.resultMaps(resultMaps);

//        countMsBuilder.keyProperty(String.join(",", ms.getKeyProperties()));
        countMsBuilder.resultOrdered(ms.isResultOrdered());
        countMsBuilder.useCache(ms.isUseCache());

        MappedStatement countMs = countMsBuilder.build();

        CacheKey countCacheKey = executor.createCacheKey(countMs, parameterObject, RowBounds.DEFAULT, boundSql);// 便于对同样的查询进行缓存
        String countSql = getCountSql(boundSql.getSql());

        BoundSql countBoundSql = new BoundSql(countMs.getConfiguration(), countSql, boundSql.getParameterMappings(), parameterObject);

        return (int)executor.query(countMs, parameterObject, RowBounds.DEFAULT, resultHandler, countCacheKey, countBoundSql).get(0);
    }

    private static String getCountSql(String sql) {
        int selectIndex = sql.toLowerCase().indexOf("select");
        int fromIndex = sql.toLowerCase().indexOf("from");
        String sub = sql.substring(selectIndex + 6, fromIndex);
        return sql.replace(sub, MS_COUNT_Infix);
    }


    public static <E> List<E> pageQuery(Executor executor, CacheKey cacheKey, MappedStatement mappedStatement, BoundSql boundSql, Object paramObject, ResultHandler resultHandler) throws SQLException {
        /**
         * 1、更新缓存key
         * 2、更新paramterMap
         * 3、更新parameterObject
         * 4、更新BoundSql
         */

        //更新cacheKey
        cacheKey.update(PageHelper.pageThreadLocal.get().getPageNum());
        cacheKey.update(PageHelper.pageThreadLocal.get().getPageSize());

        //更新parameterMap
        List<ParameterMapping> parameterMappings =  boundSql.getParameterMappings();
        ParameterMapping firstParameterMap = new ParameterMapping.Builder(mappedStatement.getConfiguration(), FIRST_PAGEHELPER, Integer.class).build();
        ParameterMapping secondParameterMap = new ParameterMapping.Builder(mappedStatement.getConfiguration(), SECOND_PAGEHELPER, Integer.class).build();
        parameterMappings.add(firstParameterMap);
        parameterMappings.add(secondParameterMap);

        //更新paramObject, 注意此处只是简单处理下，正常使用情况下，都会带上注解，此时Object的真实类型为Map。除非你不专业的使用，只有一个参数并且没有注解，此时但是一个Object，这种情况此处不予以考虑。
        //如果的确需要考虑这种情况，可以参见PageHelper中的代码，具体是AbstractHelperDialect.processParameterObject
        Map<String, Object> params = new HashMap<>();
        if (paramObject != null && paramObject instanceof Map) {
            params.putAll((Map) paramObject);
        }
        int offset = (PageHelper.pageThreadLocal.get().getPageNum() - 1) * PageHelper.pageThreadLocal.get().getPageSize();
        int limit = PageHelper.pageThreadLocal.get().getPageSize();
        params.put(FIRST_PAGEHELPER, offset);
        params.put(SECOND_PAGEHELPER, limit);

        //更新BoundSql
        String sql = boundSql.getSql();
        String pageSql = getPageSql(sql);

        BoundSql pageBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql, parameterMappings, params);

        return executor.query(mappedStatement, params, RowBounds.DEFAULT, resultHandler, cacheKey, pageBoundSql);
    }

    public static String getPageSql(String sql) {
        return sql + " LIMIT ?, ?";
    }
}
