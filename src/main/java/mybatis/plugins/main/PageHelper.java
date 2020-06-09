package mybatis.plugins.main;

public class PageHelper {
    public static ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();


    public static <T> Page<T> startPage(int pageNum, int pageSize) {
        if (pageNum < 0 || pageSize < 0) {
            throw new IllegalArgumentException("pageNum and pageSize must be Positive");
        }

        Page page = new Page<>(pageNum, pageSize);

        pageThreadLocal.set(page);

        return page;
    }
}
