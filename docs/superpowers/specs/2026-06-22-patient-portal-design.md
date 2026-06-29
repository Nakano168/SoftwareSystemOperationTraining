# 患者端系统设计规格

日期：2026-06-22

## 背景与目标

本项目实现医疗系统中的患者端，由章瑶负责。范围来自 `初步设计.docx` 中“患者端”模块：登录注册、AI 问诊、智能导诊、线上挂号/退号、电子病历查询、检查/检验报告查询、处方查询、费用查询。

第一版目标是形成稳定可演示的患者就医闭环：患者注册登录后，通过 AI 问诊获得科室和医生推荐，完成线上挂号；之后可查看自己的挂号记录、电子病历、检查/检验报告、处方和费用信息。

## 选定方案

采用 Spring Boot + MyBatis-Plus + 少量 XML/注解联表查询。

后端遵循参考项目 `StudentClub` 的分层结构：

- `controller`：接收前端请求，返回统一响应。
- `service`：处理注册登录、AI 问诊、挂号、取消挂号、查询等业务逻辑。
- `mapper`：访问数据库，基础 CRUD 使用 MyBatis-Plus，跨表展示使用 XML 或注解 SQL。
- `entity/dto`：分别承载数据库表字段和页面展示数据。

该方案兼顾课程项目风格、代码量和可维护性。基础表操作简洁，复杂列表和详情页也能通过 DTO 清晰返回。

## 数据库表使用

数据库表以 `初步设计.docx` 为准，不新增独立业务表。患者端主要使用以下表：

- `sys_user`：登录账号、密码、姓名、手机号、角色和状态。
- `patient`：患者性别、生日、身份证号、地址、过敏史、既往病史。
- `department`：科室列表、科室类型、位置和状态。
- `doctor`：医生基础信息、科室、医生类型、职称、擅长方向。
- `doctor_schedule`：医生排班、出诊日期、时间段、号源数量。
- `ai_consultation`：AI 问诊记录、主诉、症状描述、AI 摘要、推荐科室、风险等级、完整回复。
- `registration`：线上挂号和退号记录、科室、医生、排班、排队号、费用状态、挂号状态。
- `medical_record`：电子病历查询。
- `exam_lab_order`、`exam_lab_order_item`、`exam_lab_report`、`medical_item`：检查/检验申请与报告查询。
- `prescription`、`prescription_item`、`drug`：处方及药品明细查询。
- `fee_order`、`fee_order_item`、`payment_record`、`refund_record`：费用、支付和退费查询。
- `ai_call_log`：后续接入真实 AI API 时记录调用日志，第一版可预留。

## 后端接口设计

统一响应结构：

```json
{
  "success": true,
  "message": "操作成功",
  "data": {}
}
```

主要接口分组：

- `/api/patient/register`：患者注册，写入 `sys_user` 和 `patient`。
- `/api/patient/login`：患者登录，返回患者基础信息。第一版不强制 JWT，优先保证演示稳定。
- `/api/patient/info/{patientId}`：获取个人信息。
- `/api/patient/update`：修改患者资料。
- `/api/triage/consult`：本地规则模拟 AI 问诊，写入 `ai_consultation`，返回摘要、风险等级、推荐科室和推荐医生。
- `/api/department/list`：查询可用科室。
- `/api/doctor/list`：查询医生列表，支持按科室筛选。
- `/api/doctor/detail/{doctorId}`：医生详情和近期排班。
- `/api/registration/create`：创建线上挂号，写入 `registration`，扣减 `doctor_schedule.remain_quota`。
- `/api/registration/list/{patientId}`：查询我的挂号记录。
- `/api/registration/cancel/{registrationId}`：取消挂号，恢复排班剩余号源。
- `/api/medical-record/list/{patientId}`、`/api/medical-record/detail/{recordId}`：电子病历列表与详情。
- `/api/report/list/{patientId}`、`/api/report/detail/{reportId}`：检查/检验报告列表与详情。
- `/api/prescription/list/{patientId}`、`/api/prescription/detail/{prescriptionId}`：处方列表与详情。
- `/api/fee/list/{patientId}`、`/api/fee/detail/{feeOrderId}`：费用列表与详情。

## AI 问诊规则

第一版使用本地规则模拟 AI，后续可替换为真实 API。替换点集中在 `AiTriageService`，前端接口和返回结构保持不变。

规则示例：

- 发热、咳嗽、咽痛：推荐呼吸内科，风险低或中。
- 胸痛、胸闷、心悸：推荐心内科，风险中或高。
- 腹痛、腹泻、呕吐：推荐消化内科，风险低或中。
- 头痛、眩晕、肢体麻木：推荐神经内科，风险中或高。
- 儿童、孕妇、严重疼痛、高热不退等关键词提高风险等级。
- 未匹配时推荐全科或普通内科，并提示进一步线下咨询。

AI 返回内容包含：

- `aiSummary`：病情摘要。
- `riskLevel`：低风险、中风险或高风险。
- `recommendedDept`：推荐科室。
- `recommendedDoctors`：推荐医生列表。
- `aiResult`：完整导诊说明。

## 前端页面结构

前端使用 Vue 3 单页应用，以手机端形式呈现。视觉方向采用清爽医疗蓝，同时保留参考原型中的手机外壳、状态栏、卡片、柔和阴影和底部导航。

底部导航包含四个主入口：

- 首页：患者信息、AI 问诊主卡片、今日/最近挂号提醒、常用功能入口。
- AI 问诊：输入主诉和症状描述，显示导诊结果，支持一键去挂号。
- 挂号：科室筛选、医生列表、医生详情、排班选择、提交挂号、我的挂号和取消挂号。
- 我的：个人资料、电子病历、报告、处方、费用查询入口。

页面需要覆盖：

- 登录页、注册页。
- 首页。
- AI 问诊页、导诊结果页。
- 科室/医生列表页、医生详情页、确认挂号页。
- 我的挂号页。
- 电子病历列表与详情页。
- 检查/检验报告列表与详情页。
- 处方列表与详情页。
- 费用列表与详情页。
- 个人资料页。

## 数据流

1. 患者注册：前端提交账号、密码、姓名、手机号和患者资料；后端创建 `sys_user` 与 `patient`。
2. 患者登录：前端保存登录患者信息，用于后续接口请求。
3. AI 问诊：前端提交主诉和症状描述；后端本地规则生成导诊结果，保存 `ai_consultation`。
4. 推荐挂号：前端展示推荐科室和医生；患者选择医生排班后提交挂号。
5. 线上挂号：后端校验排班和剩余号源，创建 `registration` 并扣减号源。
6. 取消挂号：后端更新挂号状态，并恢复对应排班号源。
7. 查询结果：前端按患者 ID 查询病历、报告、处方和费用，后端用 DTO 返回适合页面展示的数据。

## 错误处理

- 登录失败：提示账号或密码错误。
- 注册失败：提示账号或手机号已存在，或必填信息缺失。
- AI 问诊失败：提示智能导诊暂不可用，并保留手动选择科室入口。
- 挂号失败：提示号源不足、排班不可用或医生信息不存在。
- 取消挂号失败：提示当前状态不可取消。
- 查询为空：显示空状态，而不是空白页面。
- 前端接口请求显示 loading，失败时展示中文提示。

## 验证标准

- 后端能正常启动。
- 主要接口可通过前端或接口工具调用成功。
- 前端能跑通完整演示流程：注册/登录、AI 问诊、推荐医生、线上挂号、取消挂号、查询病历/报告/处方/费用。
- 手机端页面无文字重叠、按钮溢出、底部导航遮挡和明显布局错位。
- 本地 AI 模拟逻辑集中在服务层，后续可替换真实 API。
