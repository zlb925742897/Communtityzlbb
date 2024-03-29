package majiangshequ.community.mapper;

import majiangshequ.community.Model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modified) values(#{name},#{AccountId},#{token},#{gmtCreate},#{gmtModified})")
    public void insert(User user);
}
