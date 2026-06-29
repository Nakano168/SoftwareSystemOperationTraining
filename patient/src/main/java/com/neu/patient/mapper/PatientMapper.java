package com.neu.patient.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.patient.entity.Patient;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
    @Select("SELECT * FROM patient WHERE patient_no = #{patientNo}")
    Patient findByPatientNo(String patientNo);
    
    @Select("SELECT * FROM patient WHERE user_id = #{userId}")
    Patient findByUserId(Long userId);
}
