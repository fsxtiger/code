package mybatis.mapper;

import mybatis.domain.Blog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface BlogMapper {
    @Select("SELECT * FROM blog where id > #{id} and title <> #{title}")
    List<Blog> selectBlog(@Param("title") String title, @Param("id") int id);
}