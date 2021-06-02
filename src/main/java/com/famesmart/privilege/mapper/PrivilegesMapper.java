package com.famesmart.privilege.mapper;

import com.famesmart.privilege.entity.Privileges;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-27
 */
public interface PrivilegesMapper extends BaseMapper<Privileges> {

    @Select("select resource from saas_privileges where deleted_at is null group by resource")
    List<String> selectAllResource();

    @Select("select * from saas_privileges where resource = #{resource} and deleted_at is null")
    List<Privileges> selectByResource(String resource);
}
