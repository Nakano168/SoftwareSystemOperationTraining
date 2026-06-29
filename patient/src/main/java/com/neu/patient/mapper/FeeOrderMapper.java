package com.neu.patient.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.patient.entity.FeeOrder;
import java.util.List;

@Mapper
public interface FeeOrderMapper extends BaseMapper<FeeOrder> {
    @Select("SELECT * FROM fee_order WHERE patient_id = #{patientId} ORDER BY created_at DESC")
    List<FeeOrder> findByPatientId(Long patientId);
    
    @Select("SELECT * FROM fee_order WHERE patient_id = #{patientId} AND status = '待支付' ORDER BY created_at DESC")
    List<FeeOrder> findUnpaidByPatientId(Long patientId);
}
