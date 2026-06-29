package com.neu.patient.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.patient.entity.Department;
import java.util.List;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
    @Select("SELECT * FROM department WHERE status = 1")
    List<Department> findAllActive();
}
