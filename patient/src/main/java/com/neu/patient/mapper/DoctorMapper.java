package com.neu.patient.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.patient.entity.Doctor;
import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
    @Select("SELECT d.*, u.real_name AS realName FROM doctor d LEFT JOIN sys_user u ON d.user_id = u.user_id WHERE d.dept_id = #{deptId} AND d.status = 1")
    List<Doctor> findByDeptId(Long deptId);
    
    @Select("SELECT d.*, u.real_name AS realName FROM doctor d LEFT JOIN sys_user u ON d.user_id = u.user_id WHERE d.status = 1")
    List<Doctor> findAllActive();
}
