package com.neu.patient.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.patient.entity.SysUser;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND password = #{password}")
    SysUser login(String username, String password);
    
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser findByUsername(String username);

    @Select("SELECT u.* FROM sys_user u JOIN sys_role r ON u.role_id = r.role_id " +
            "WHERE u.username = #{username} AND u.password = #{password} AND r.role_code = 'PATIENT'")
    SysUser loginPatient(String username, String password);
}
