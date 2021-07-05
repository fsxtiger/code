package mybatis;

import mybatis.domain.Blog;
import mybatis.mapper.BlogMapper;
import mybatis.plugins.main.Page;
import mybatis.plugins.main.PageHelper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileInputStream;
import java.util.List;

public class MybatisDemo {
    public static void main(String[] args) throws Exception {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(new FileInputStream("mybatis-config.xml"));
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);

            Page page = PageHelper.startPage(1, 5);
            List<Blog> blog = blogMapper.selectBlog("202", 1);
            System.out.println(blog);
            System.out.println(page.getTotal());
        }
    }
}
