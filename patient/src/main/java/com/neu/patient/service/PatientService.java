package com.neu.patient.service;

import com.neu.patient.entity.*;

public interface PatientService {
    // 患者注册
    int register(SysUser user, Patient patient);
    // 患者登录
    SysUser login(String username, String password);
    // 获取患者信息
    Patient getPatientInfo(Long patientId);
    Patient getPatientByUserId(Long userId);
    // 更新患者信息
    boolean updatePatient(Patient patient);
    // 获取当前登录患者
    Patient getCurrentPatient(Long userId);
}
