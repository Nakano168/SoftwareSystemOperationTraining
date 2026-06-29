# Patient Portal Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the patient-side medical portal with local AI triage, online registration/cancellation, and patient record/report/prescription/fee queries.

**Architecture:** The backend follows the existing Spring Boot layered style from the `StudentClub` reference: controller, service, mapper, entity, dto. The frontend is a Vue 3 single-page mobile shell with four primary tabs: Home, AI Consult, Registration, and Mine.

**Tech Stack:** Spring Boot 3.5, Java 17, MyBatis-Plus, MySQL, Vue 3, Vue Router, Axios, Element Plus icons.

---

## File Structure

Backend root: `H:\Experiment\Code\SoftwareSystemOperationTraining\patient`

- Modify `patient/pom.xml`: replace plain MyBatis starter with MyBatis-Plus starter and keep MySQL/web/test dependencies.
- Modify `patient/src/main/resources/application.properties`: configure port, context path, datasource, MyBatis-Plus aliases and mapper XML locations.
- Create `patient/src/main/java/com/neu/patient/config/WebMvcConfig.java`: allow Vue dev server requests.
- Create `patient/src/main/java/com/neu/patient/common/Result.java`: common API response.
- Create `patient/src/main/java/com/neu/patient/entity/*.java`: database table entities matching `初步设计.docx`.
- Create `patient/src/main/java/com/neu/patient/dto/*.java`: request/response DTOs for patient pages.
- Create `patient/src/main/java/com/neu/patient/mapper/*.java`: MyBatis-Plus mappers and custom query methods.
- Create `patient/src/main/resources/mapper/*.xml`: custom login, doctor, registration, record, report, prescription, fee queries.
- Create `patient/src/main/java/com/neu/patient/service/*.java`: service interfaces.
- Create `patient/src/main/java/com/neu/patient/service/impl/*.java`: service implementations.
- Create `patient/src/main/java/com/neu/patient/controller/*.java`: REST controllers under `/api/...`.
- Modify `patient/src/test/java/com/neu/patient/PatientApplicationTests.java`: lightweight Spring context test.

Frontend root: `H:\Experiment\Code\SoftwareSystemOperationTraining\vue`

- Modify `vue/vue.config.js`: proxy `/api` to backend port `8088`.
- Modify `vue/src/main.js`: import global CSS.
- Modify `vue/src/router/index.js`: define login/register/main routes.
- Modify `vue/src/App.vue`: keep router outlet.
- Create `vue/src/api/request.js`: Axios instance and response handling.
- Create `vue/src/api/patient.js`: patient portal API wrappers.
- Create `vue/src/styles/mobile.css`: phone shell, medical-blue palette, cards, tabs, forms.
- Create `vue/src/views/LoginView.vue`: login/register entry.
- Create `vue/src/views/RegisterView.vue`: patient registration form.
- Create `vue/src/views/PatientShell.vue`: mobile frame and bottom navigation.
- Create `vue/src/views/HomeView.vue`: home dashboard.
- Create `vue/src/views/TriageView.vue`: local AI consult flow.
- Create `vue/src/views/RegistrationView.vue`: department/doctor/schedule registration flow.
- Create `vue/src/views/MineView.vue`: profile and query entry points.
- Create `vue/src/views/QueryListView.vue`: reusable list page for medical records/reports/prescriptions/fees.
- Create `vue/src/views/QueryDetailView.vue`: reusable detail page.

---

### Task 1: Backend Dependencies And Configuration

**Files:**
- Modify: `H:\Experiment\Code\SoftwareSystemOperationTraining\patient\pom.xml`
- Modify: `H:\Experiment\Code\SoftwareSystemOperationTraining\patient\src\main\resources\application.properties`
- Create: `H:\Experiment\Code\SoftwareSystemOperationTraining\patient\src\main\java\com\neu\patient\config\WebMvcConfig.java`
- Create: `H:\Experiment\Code\SoftwareSystemOperationTraining\patient\src\main\java\com\neu\patient\common\Result.java`
- Modify: `H:\Experiment\Code\SoftwareSystemOperationTraining\patient\src\test\java\com\neu\patient\PatientApplicationTests.java`

- [ ] **Step 1: Add MyBatis-Plus dependency**

Replace the MyBatis starter in `pom.xml` with:

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.7</version>
</dependency>
```

Keep `spring-boot-starter-web`, `mysql-connector-j`, and `spring-boot-starter-test`.

- [ ] **Step 2: Configure backend application properties**

Write `application.properties` as:

```properties
spring.application.name=patient

server.port=8088

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/software_system_operation_training?serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=776803

mybatis-plus.mapper-locations=classpath:/mapper/*.xml
mybatis-plus.type-aliases-package=com.neu.patient.entity
mybatis-plus.configuration.map-underscore-to-camel-case=true

logging.level.com.neu.patient.mapper=debug
```

If the local database name or password differs, change only `spring.datasource.url` and `spring.datasource.password`.

- [ ] **Step 3: Add CORS configuration**

Create `WebMvcConfig.java`:

```java
package com.neu.patient.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(36000);
    }
}
```

- [ ] **Step 4: Add common response wrapper**

Create `Result.java`:

```java
package com.neu.patient.common;

public class Result<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> ok(String message, T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
```

- [ ] **Step 5: Run backend test**

Run:

```powershell
cd H:\Experiment\Code\SoftwareSystemOperationTraining\patient
.\mvnw.cmd test
```

Expected: Maven compiles and the Spring context test passes, or fails only because datasource credentials/database are not available locally. If datasource fails, continue implementation but record the failure.

---

### Task 2: Backend Entities, DTOs, And Mappers

**Files:**
- Create: `patient/src/main/java/com/neu/patient/entity/SysUser.java`
- Create: `patient/src/main/java/com/neu/patient/entity/Patient.java`
- Create: `patient/src/main/java/com/neu/patient/entity/Department.java`
- Create: `patient/src/main/java/com/neu/patient/entity/Doctor.java`
- Create: `patient/src/main/java/com/neu/patient/entity/DoctorSchedule.java`
- Create: `patient/src/main/java/com/neu/patient/entity/AiConsultation.java`
- Create: `patient/src/main/java/com/neu/patient/entity/Registration.java`
- Create: `patient/src/main/java/com/neu/patient/entity/MedicalRecord.java`
- Create: `patient/src/main/java/com/neu/patient/entity/ExamLabReport.java`
- Create: `patient/src/main/java/com/neu/patient/entity/Prescription.java`
- Create: `patient/src/main/java/com/neu/patient/entity/FeeOrder.java`
- Create: `patient/src/main/java/com/neu/patient/dto/*.java`
- Create: `patient/src/main/java/com/neu/patient/mapper/*.java`
- Create: `patient/src/main/resources/mapper/PatientPortalMapper.xml`

- [ ] **Step 1: Create table entities**

For each entity, use `@TableName`, `@TableId`, Java camelCase fields matching the Word table columns, and plain getters/setters. Example for `Patient.java`:

```java
package com.neu.patient.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;

@TableName("patient")
public class Patient {
    @TableId(value = "patient_id", type = IdType.AUTO)
    private Long patientId;
    private Long userId;
    private String gender;
    private LocalDate birthday;
    private String idCard;
    private String address;
    private String allergyHistory;
    private String pastHistory;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getAllergyHistory() { return allergyHistory; }
    public void setAllergyHistory(String allergyHistory) { this.allergyHistory = allergyHistory; }
    public String getPastHistory() { return pastHistory; }
    public void setPastHistory(String pastHistory) { this.pastHistory = pastHistory; }
}
```

Use the same pattern for the other entity classes.

- [ ] **Step 2: Create request DTOs**

Create DTOs with getters/setters:

```java
public class PatientRegisterRequest {
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String gender;
    private String birthday;
    private String idCard;
    private String address;
    private String allergyHistory;
    private String pastHistory;
}
```

Also create:

- `PatientLoginRequest`: `username`, `password`.
- `TriageRequest`: `patientId`, `chiefComplaint`, `symptomDetail`.
- `RegistrationCreateRequest`: `patientId`, `consultationId`, `deptId`, `doctorId`, `scheduleId`.

- [ ] **Step 3: Create response DTOs**

Create:

- `PatientProfileDTO`: patient and user display fields.
- `DoctorDTO`: doctorId, realName, deptId, deptName, title, specialty, remainQuota, nextWorkDate, timePeriod.
- `ScheduleDTO`: scheduleId, doctorId, deptId, workDate, timePeriod, totalQuota, remainQuota, status.
- `TriageResultDTO`: consultationId, aiSummary, riskLevel, recommendedDeptId, recommendedDeptName, aiResult, recommendedDoctors.
- `RegistrationDTO`: registrationId, registrationNo, queueNo, deptName, doctorName, workDate, timePeriod, feeStatus, status, createdAt.
- `MedicalRecordDTO`, `ReportDTO`, `PrescriptionDTO`, `FeeDTO`: page display fields for each query module.

- [ ] **Step 4: Create mapper interfaces**

For each entity create `BaseMapper` mapper. Example:

```java
package com.neu.patient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.patient.entity.Patient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
```

Create `PatientPortalMapper` for custom joined DTO queries.

- [ ] **Step 5: Add custom SQL XML**

Create `PatientPortalMapper.xml` with namespace `com.neu.patient.mapper.PatientPortalMapper`. Include SQL for:

- login/profile by username/password.
- doctor list with department and user real name.
- schedules by doctor.
- registration list by patient.
- medical record list/detail by patient.
- report list/detail by patient.
- prescription list/detail by patient.
- fee list/detail by patient.

- [ ] **Step 6: Compile backend**

Run:

```powershell
cd H:\Experiment\Code\SoftwareSystemOperationTraining\patient
.\mvnw.cmd -DskipTests package
```

Expected: Java compilation succeeds. SQL XML may not be runtime-tested yet.

---

### Task 3: Backend Patient, Doctor, And Registration Services

**Files:**
- Create: `patient/src/main/java/com/neu/patient/service/PatientService.java`
- Create: `patient/src/main/java/com/neu/patient/service/DoctorService.java`
- Create: `patient/src/main/java/com/neu/patient/service/RegistrationService.java`
- Create: `patient/src/main/java/com/neu/patient/service/impl/PatientServiceImpl.java`
- Create: `patient/src/main/java/com/neu/patient/service/impl/DoctorServiceImpl.java`
- Create: `patient/src/main/java/com/neu/patient/service/impl/RegistrationServiceImpl.java`
- Create: `patient/src/main/java/com/neu/patient/controller/PatientController.java`
- Create: `patient/src/main/java/com/neu/patient/controller/DoctorController.java`
- Create: `patient/src/main/java/com/neu/patient/controller/DepartmentController.java`
- Create: `patient/src/main/java/com/neu/patient/controller/RegistrationController.java`

- [ ] **Step 1: Implement patient registration and login**

`PatientService.register` validates username/password/realName/phone, checks `sys_user.username` uniqueness, inserts `sys_user`, then inserts `patient`.

`PatientService.login` checks username/password and returns `PatientProfileDTO`.

- [ ] **Step 2: Implement department and doctor queries**

`DoctorService` returns:

- active department list from `department`.
- doctor list by optional `deptId`.
- doctor detail with schedules.

Only include doctors where `doctor.status = 1` when the field is present.

- [ ] **Step 3: Implement registration create**

`RegistrationService.create`:

1. Load schedule by `scheduleId`.
2. Reject if schedule missing, not available, or `remainQuota <= 0`.
3. Generate registration number as `REG` + current timestamp.
4. Use queue number as `totalQuota - remainQuota + 1`.
5. Insert `registration` with `source = 线上挂号`, `feeStatus = 未支付`, `status = 已挂号`.
6. Decrease `doctor_schedule.remain_quota` by 1.

- [ ] **Step 4: Implement registration cancel**

`RegistrationService.cancel`:

1. Load registration by ID.
2. Reject if missing or status is not `已挂号`.
3. Update status to `已取消`.
4. Increase linked schedule `remain_quota` by 1.

- [ ] **Step 5: Add controllers**

Controllers return `Result.ok(...)` and `Result.fail(...)`:

- `POST /api/patient/register`
- `POST /api/patient/login`
- `GET /api/patient/info/{patientId}`
- `PUT /api/patient/update`
- `GET /api/department/list`
- `GET /api/doctor/list`
- `GET /api/doctor/detail/{doctorId}`
- `POST /api/registration/create`
- `GET /api/registration/list/{patientId}`
- `POST /api/registration/cancel/{registrationId}`

- [ ] **Step 6: Compile**

Run:

```powershell
cd H:\Experiment\Code\SoftwareSystemOperationTraining\patient
.\mvnw.cmd -DskipTests package
```

Expected: Build succeeds.

---

### Task 4: Backend Local AI Triage And Patient Query Services

**Files:**
- Create: `patient/src/main/java/com/neu/patient/service/TriageService.java`
- Create: `patient/src/main/java/com/neu/patient/service/PatientQueryService.java`
- Create: `patient/src/main/java/com/neu/patient/service/impl/TriageServiceImpl.java`
- Create: `patient/src/main/java/com/neu/patient/service/impl/PatientQueryServiceImpl.java`
- Create: `patient/src/main/java/com/neu/patient/controller/TriageController.java`
- Create: `patient/src/main/java/com/neu/patient/controller/MedicalRecordController.java`
- Create: `patient/src/main/java/com/neu/patient/controller/ReportController.java`
- Create: `patient/src/main/java/com/neu/patient/controller/PrescriptionController.java`
- Create: `patient/src/main/java/com/neu/patient/controller/FeeController.java`

- [ ] **Step 1: Implement local AI triage rules**

`TriageServiceImpl.consult` combines chief complaint and symptom detail, then matches:

- `发热|咳嗽|咽痛|气喘` -> `呼吸内科`
- `胸痛|胸闷|心悸|心慌` -> `心内科`
- `腹痛|腹泻|呕吐|胃痛` -> `消化内科`
- `头痛|眩晕|麻木|抽搐` -> `神经内科`
- default -> `普通内科`

Risk rules:

- If text contains `胸痛|昏迷|呼吸困难|剧烈|高热不退`, risk is `高风险`.
- Else if text contains `持续|反复|加重|孕妇|儿童|老人`, risk is `中风险`.
- Else risk is `低风险`.

Persist `ai_consultation` with the recommended department ID if a matching department exists.

- [ ] **Step 2: Return recommended doctors**

After finding the recommended department, query doctors from that department and include up to three doctors in `TriageResultDTO.recommendedDoctors`.

- [ ] **Step 3: Implement patient query service**

`PatientQueryService` uses `PatientPortalMapper` DTO queries:

- `listMedicalRecords(patientId)`, `getMedicalRecord(recordId)`
- `listReports(patientId)`, `getReport(reportId)`
- `listPrescriptions(patientId)`, `getPrescription(prescriptionId)`
- `listFees(patientId)`, `getFee(feeOrderId)`

Return empty lists when no records exist.

- [ ] **Step 4: Add controllers**

Add:

- `POST /api/triage/consult`
- `GET /api/medical-record/list/{patientId}`
- `GET /api/medical-record/detail/{recordId}`
- `GET /api/report/list/{patientId}`
- `GET /api/report/detail/{reportId}`
- `GET /api/prescription/list/{patientId}`
- `GET /api/prescription/detail/{prescriptionId}`
- `GET /api/fee/list/{patientId}`
- `GET /api/fee/detail/{feeOrderId}`

- [ ] **Step 5: Build backend**

Run:

```powershell
cd H:\Experiment\Code\SoftwareSystemOperationTraining\patient
.\mvnw.cmd -DskipTests package
```

Expected: Build succeeds.

---

### Task 5: Frontend API Layer And Routing

**Files:**
- Modify: `vue/vue.config.js`
- Modify: `vue/src/main.js`
- Modify: `vue/src/router/index.js`
- Create: `vue/src/api/request.js`
- Create: `vue/src/api/patient.js`
- Create: `vue/src/styles/mobile.css`

- [ ] **Step 1: Configure proxy**

Set proxy target:

```js
target: 'http://localhost:8088'
```

- [ ] **Step 2: Add Axios request wrapper**

`request.js`:

```js
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.response.use(
  response => {
    const body = response.data
    if (body && body.success === false) {
      ElMessage.error(body.message || '操作失败')
      return Promise.reject(new Error(body.message || '操作失败'))
    }
    return body ? body.data : response.data
  },
  error => {
    ElMessage.error(error.message || '网络请求失败')
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **Step 3: Add patient API module**

`patient.js` exports functions for every backend endpoint: `login`, `register`, `consultTriage`, `listDepartments`, `listDoctors`, `getDoctorDetail`, `createRegistration`, `listRegistrations`, `cancelRegistration`, `listMedicalRecords`, `getMedicalRecord`, `listReports`, `getReport`, `listPrescriptions`, `getPrescription`, `listFees`, `getFee`.

- [ ] **Step 4: Define router**

Routes:

- `/login` -> `LoginView`
- `/register` -> `RegisterView`
- `/` -> redirect `/app/home`
- `/app` -> `PatientShell`, children: `home`, `triage`, `registration`, `mine`, `query/:type`, `detail/:type/:id`

- [ ] **Step 5: Add global mobile CSS**

Import `./styles/mobile.css` in `main.js`. CSS defines `--primary`, `--bg`, `.phone`, `.status`, `.topbar`, `.content`, `.card`, `.bottom-nav`, `.btn-primary`, `.field`, `.chip`, `.empty`.

- [ ] **Step 6: Build frontend**

Run:

```powershell
cd H:\Experiment\Code\SoftwareSystemOperationTraining\vue
npm run build
```

Expected: Vue build succeeds.

---

### Task 6: Frontend Login, Shell, Home, Triage, And Registration Pages

**Files:**
- Create: `vue/src/views/LoginView.vue`
- Create: `vue/src/views/RegisterView.vue`
- Create: `vue/src/views/PatientShell.vue`
- Create: `vue/src/views/HomeView.vue`
- Create: `vue/src/views/TriageView.vue`
- Create: `vue/src/views/RegistrationView.vue`

- [ ] **Step 1: Implement login/register**

Use localStorage key `patientUser`. Login stores returned patient profile and redirects to `/app/home`. Register submits patient fields and then redirects to `/login`.

- [ ] **Step 2: Implement mobile shell**

`PatientShell` renders:

- black rounded phone frame.
- status bar.
- router child content.
- bottom nav with Home, AI问诊, 挂号, 我的.

- [ ] **Step 3: Implement home**

Home reads `patientUser`, displays greeting, AI consult card, latest registration card, and shortcuts to query pages.

- [ ] **Step 4: Implement triage**

Triage page:

- inputs `chiefComplaint`, `symptomDetail`.
- submit calls `consultTriage`.
- shows aiSummary, riskLevel, recommended department, recommended doctors.
- clicking a recommended doctor opens Registration tab with selected doctor context if available; otherwise routes to `/app/registration`.

- [ ] **Step 5: Implement registration page**

Registration page:

- loads departments and doctors.
- filters doctors by department.
- shows doctor cards and schedule cards.
- submitting calls `createRegistration`.
- shows my registrations and cancel buttons.

- [ ] **Step 6: Build frontend**

Run:

```powershell
cd H:\Experiment\Code\SoftwareSystemOperationTraining\vue
npm run build
```

Expected: Build succeeds.

---

### Task 7: Frontend Mine And Query Pages

**Files:**
- Create: `vue/src/views/MineView.vue`
- Create: `vue/src/views/QueryListView.vue`
- Create: `vue/src/views/QueryDetailView.vue`
- Modify: `vue/src/router/index.js`

- [ ] **Step 1: Implement mine page**

Mine displays patient profile and cards for:

- 电子病历
- 检查检验报告
- 处方查询
- 费用查询
- 退出登录

- [ ] **Step 2: Implement query list**

`QueryListView` maps `type`:

- `records` -> `listMedicalRecords`
- `reports` -> `listReports`
- `prescriptions` -> `listPrescriptions`
- `fees` -> `listFees`

Show loading, empty state, and list cards. Card click routes to detail.

- [ ] **Step 3: Implement query detail**

`QueryDetailView` maps `type` and `id` to detail API and renders common field rows from the returned object.

- [ ] **Step 4: Build frontend**

Run:

```powershell
cd H:\Experiment\Code\SoftwareSystemOperationTraining\vue
npm run build
```

Expected: Build succeeds.

---

### Task 8: End-To-End Verification

**Files:**
- Read/verify: backend and frontend files from previous tasks.

- [ ] **Step 1: Run backend build**

Run:

```powershell
cd H:\Experiment\Code\SoftwareSystemOperationTraining\patient
.\mvnw.cmd -DskipTests package
```

Expected: build succeeds.

- [ ] **Step 2: Run frontend build**

Run:

```powershell
cd H:\Experiment\Code\SoftwareSystemOperationTraining\vue
npm run build
```

Expected: build succeeds.

- [ ] **Step 3: Start backend**

Run:

```powershell
cd H:\Experiment\Code\SoftwareSystemOperationTraining\patient
.\mvnw.cmd spring-boot:run
```

Expected: backend starts on port `8088`.

- [ ] **Step 4: Start frontend**

Run:

```powershell
cd H:\Experiment\Code\SoftwareSystemOperationTraining\vue
npm run serve
```

Expected: frontend starts on port `8080`.

- [ ] **Step 5: Browser smoke test**

Open `http://localhost:8080` and verify:

- login page renders inside app.
- after login, phone shell fits on desktop.
- bottom nav switches Home, AI问诊, 挂号, 我的.
- AI问诊 result card renders without overlap.
- registration cards and cancel buttons fit the mobile frame.
- query list/detail pages show empty states or data without layout issues.

- [ ] **Step 6: Record remaining environment issues**

If database credentials, missing seed data, or port conflicts prevent full runtime verification, record the exact blocker and the commands that did pass.

---

## Self-Review

- Spec coverage: The plan covers backend configuration, entities/DTOs/mappers, patient registration/login, doctor and department queries, local AI triage, online registration/cancellation, patient medical record/report/prescription/fee queries, Vue mobile shell, all main pages, and verification.
- Placeholder scan: No TBD/TODO placeholders are present.
- Type consistency: DTO and service names are stable across tasks; endpoint names match the design spec.
