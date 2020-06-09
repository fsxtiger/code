package mybatis.plugins.main;

import mybatis.plugins.util.ExecutorUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class PageInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Executor executor = (Executor) invocation.getTarget();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameterObject = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];


        BoundSql boundSql;
        CacheKey cacheKey;
        if (args.length == 6) {
            boundSql = (BoundSql) args[5];
            cacheKey = (CacheKey) args[4];
        } else {
            boundSql = ms.getBoundSql(parameterObject);
            cacheKey = executor.createCacheKey(ms, parameterObject, rowBounds, boundSql);
        }


        if (PageHelper.pageThreadLocal.get() == null) {
            //如果没有放置，说明，没有设置分页，请求直接放过
            return invocation.proceed();
        }
        if (PageHelper.pageThreadLocal.get().isCount()) {
            //设置了查询总数，需要先进行总数查询
            int count = ExecutorUtil.count(executor, ms, parameterObject, resultHandler, boundSql);
            PageHelper.pageThreadLocal.get().setTotal(count);

            //进行count判断，如果pageSize 大于count 直接返回
            if (PageHelper.pageThreadLocal.get().getPageSize() * PageHelper.pageThreadLocal.get().getPageNum() > count) {
                return PageHelper.pageThreadLocal.get();
            }

            PageHelper.pageThreadLocal.get().addAll(ExecutorUtil.pageQuery(executor, cacheKey, ms, boundSql, parameterObject, resultHandler));
            return PageHelper.pageThreadLocal.get();
        }
        PageHelper.pageThreadLocal.get().addAll(ExecutorUtil.pageQuery(executor, cacheKey, ms, boundSql, parameterObject, resultHandler));
        return PageHelper.pageThreadLocal.get();
    }
}
