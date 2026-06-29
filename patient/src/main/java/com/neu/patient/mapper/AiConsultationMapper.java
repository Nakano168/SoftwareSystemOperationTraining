package com.neu.patient.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.patient.entity.AiConsultation;
import java.util.List;

@Mapper
public interface AiConsultationMapper extends BaseMapper<AiConsultation> {
    @Select("SELECT * FROM ai_consultation WHERE patient_id = #{patientId} ORDER BY created_at DESC")
    List<AiConsultation> findByPatientId(Long patientId);
}
