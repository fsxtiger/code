package mybatis.plugins.main;

import java.util.ArrayList;

public class Page<T> extends ArrayList<T> {
    private int total;
    private int pageNum;
    private int pageSize;
    private boolean count;


    public Page(int pageNum, int pageSize) {
        this(pageNum, pageSize, true);
    }

    public Page(int pageNum, int pageSize, boolean count) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }
}
