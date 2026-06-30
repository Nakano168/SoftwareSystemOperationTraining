<template>
  <div class="patient-page">
    <div class="patient-topbar">
      <span @click="$router.push('/patient/home')" class="patient-back">首页</span>
      <span class="patient-title">我的挂号</span>
      <span></span>
    </div>

    <div class="patient-tabs">
      <button class="patient-tab" :class="{ active: tab === 'all' }" @click="tab='all'">全部</button>
      <button class="patient-tab" :class="{ active: tab === '待确认' }" @click="tab='待确认'">待确认</button>
      <button class="patient-tab" :class="{ active: tab === '已退号' }" @click="tab='已退号'">已退号</button>
    </div>

    <div class="patient-content appointment-list">
      <div class="record-card patient-card" v-for="r in filteredList" :key="r.registrationId">
        <div class="card-head">
          <span class="patient-status" :class="statusClass(r.status)">{{ registrationStatusText(r.status) }}</span>
          <span class="date">{{ r.registeredAt?.substring(0,10) }}</span>
        </div>
        <div class="patient-row"><span class="label">挂号编号</span><span class="value">{{ r.registrationNo }}</span></div>
        <div class="patient-row"><span class="label">科室</span><span class="value">{{ r.deptName || r.deptId }}</span></div>
        <div class="patient-row"><span class="label">医生</span><span class="value">{{ r.doctorName || '医生 ' + r.doctorId }}</span></div>
        <div class="patient-row"><span class="label">费用</span><span class="value fee">¥{{ r.registrationFee }}</span></div>
        <div class="patient-row">
          <span class="label">缴费状态</span>
          <span class="value" :class="feeStatusClass(r.feeStatus)">{{ feeStatusText(r.feeStatus) }}</span>
        </div>
        <div class="card-actions" v-if="r.status === '待确认'">
          <button
            class="patient-button secondary"
            :disabled="cancelingId === r.registrationId"
            @click="cancelReg(r.registrationId)"
          >
            {{ cancelingId === r.registrationId ? '取消中...' : '取消挂号' }}
          </button>
          <button v-if="r.feeStatus === '待支付'" class="patient-button" @click="payFee(r)">去缴费</button>
        </div>
      </div>
      <div v-if="filteredList.length === 0" class="patient-empty">暂无挂号记录</div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import feedback from '@/utils/feedback'
import { feeStatusText, registrationStatusText } from '@/utils/statusLabels'
export default {
  data() {
    return {
      tab: 'all',
      registrations: [],
      patientId: null,
      cancelingId: null
    }
  },
  computed: {
    filteredList() {
      if (this.tab === 'all') return this.registrations
      return this.registrations.filter(r => r.status === this.tab)
    }
  },
  async mounted() {
    await this.loadPatient()
    if (this.patientId) await this.loadData()
  },
  methods: {
    registrationStatusText,
    feeStatusText,
    statusClass(status) {
      if (status === '待确认' || status === '接诊中' || status === 'registered') return 'info'
      if (status === '已完成' || status === 'completed') return 'success'
      if (status === '已退号' || status === '爽约' || status === 'returned') return 'danger'
      return ''
    },
    feeStatusClass(status) {
      if (status === '待支付' || status === 'unpaid') return 'warn'
      if (status === '已退费' || status === 'refunded') return 'info-text'
      return 'success'
    },
    async loadPatient() {
      const userId = sessionStorage.getItem('userId')
      if (!userId) return
      const res = await axios.get(`/api/patient/info/${userId}`)
      if (res.data.success && res.data.data) this.patientId = res.data.data.patientId
    },
    async loadData() {
      const res = await axios.get(`/api/registration/my/${this.patientId}`)
      if (res.data.success) this.registrations = res.data.data
    },
    async cancelReg(id) {
       if (!id) {
         feedback.toast('缺少挂号ID')
         return
       }
       if (!(await feedback.confirm('确认取消挂号？'))) return
       this.cancelingId = id
       try {
         const res = await axios.post(`/api/registration/cancel/${id}`)
         if (res.data.success) {
           feedback.toast('取消成功')
           await this.loadData()
         } else {
           feedback.toast(res.data.message || '取消失败')
         }
       } catch (e) {
         feedback.toast(e.response?.data?.message || '操作失败')
       } finally {
         this.cancelingId = null
       }
    },
    async payFee(r) {
      try {
        const feeRes = await axios.get(`/api/fee/unpaid/${this.patientId}`)
        const fees = feeRes.data.data || []
        const match = fees.find(f => f.registrationId === r.registrationId)
        if (match) this.$router.push({ path: '/patient/fees', query: { feeOrderId: match.feeOrderId } })
        else feedback.toast('暂未生成缴费单')
      } catch(e) { feedback.toast('操作失败') }
    }
  }
}
</script>

<style scoped>
.appointment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-card {
  padding: 14px;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.date {
  font-size: 12px;
  color: var(--ink-muted);
}

.fee {
  color: var(--medical-red);
  font-weight: 800;
}

.warn {
  color: var(--medical-orange);
  font-weight: 800;
}

.success {
  color: var(--medical-green);
  font-weight: 800;
}

.info-text {
  color: var(--medical-blue);
  font-weight: 800;
}

.card-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--line-soft);
}
</style>
