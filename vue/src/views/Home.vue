<template>
  <div class="home-page">
    <section class="hero-card">
      <div>
        <div class="eyebrow">患者服务平台</div>
        <h2>{{ timeGreeting }}，{{ realName || '患者' }}</h2>
        <p>预约挂号、报告查询、费用缴纳，一站式管理就医事项。</p>
      </div>
      <div class="avatar">{{ (realName || '患').charAt(0) }}</div>
    </section>

    <section class="quick-actions">
      <button class="action-card primary" @click="goTo('/patient/appointment')">
        <span class="action-symbol">+</span>
        <span>预约挂号</span>
      </button>
      <button class="action-card" @click="$router.push('/patient/my-appointments')">
        <span class="action-symbol">≡</span>
        <span>我的挂号</span>
      </button>
      <button class="action-card" @click="$router.push('/patient/medical-records')">
        <span class="action-symbol">病</span>
        <span>病历查询</span>
      </button>
      <button class="action-card" @click="$router.push('/patient/fees')">
        <span class="action-symbol">¥</span>
        <span>在线缴费</span>
      </button>
    </section>

    <section class="patient-content">
      <div class="patient-section-title">快捷功能</div>
      <div class="feature-grid">
        <button class="feature-card patient-card" @click="$router.push('/patient/prescriptions')">
          <span class="feature-icon">Rx</span>
          <span>我的处方</span>
        </button>
        <button class="feature-card patient-card" @click="$router.push('/patient/exam-reports')">
          <span class="feature-icon">检</span>
          <span>检查报告</span>
        </button>
        <button class="feature-card patient-card" @click="$router.push('/patient/ai-center')">
          <span class="feature-icon">AI</span>
          <span>AI 就医</span>
        </button>
        <button class="feature-card patient-card" @click="$router.push('/patient/profile')">
          <span class="feature-icon">我</span>
          <span>个人信息</span>
        </button>
      </div>

      <div class="patient-section-title">待办事项</div>
      <button class="todo-card patient-card" v-if="unpaidCount > 0" @click="$router.push('/patient/fees')">
        <span class="todo-dot warning">!</span>
        <span>您有 {{ unpaidCount }} 笔待缴费订单</span>
        <span class="todo-arrow">›</span>
      </button>
      <div class="todo-card patient-card" v-else>
        <span class="todo-dot success">✓</span>
        <span>暂无待办事项</span>
      </div>
    </section>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      realName: sessionStorage.getItem('realName') || '',
      unpaidCount: 0
    }
  },
  computed: {
    timeGreeting() {
      const hour = new Date().getHours()
      if (hour < 6) return '凌晨好'
      if (hour < 12) return '早上好'
      if (hour < 18) return '下午好'
      return '晚上好'
    }
  },
  mounted() {
    this.loadUnpaid()
  },
  methods: {
    goTo(path) {
      this.$router.push(path)
    },
    async loadUnpaid() {
      const userId = sessionStorage.getItem('userId')
      if (!userId) return
      try {
        const res = await axios.get(`/api/patient/info/${userId}`)
        if (res.data.success && res.data.data) {
          const pid = res.data.data.patientId
          const feeRes = await axios.get(`/api/fee/unpaid/${pid}`)
          if (feeRes.data.success) this.unpaidCount = feeRes.data.data.length
        }
      } catch (e) {}
    }
  }
}
</script>

<style scoped>
.home-page {
  min-height: 100%;
  background: var(--page-bg);
}

.hero-card {
  margin: 0;
  padding: 24px 20px 38px;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  background: linear-gradient(135deg, var(--medical-blue), var(--medical-blue-dark));
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 0;
  opacity: 0.9;
  margin-bottom: 10px;
}

.hero-card h2 {
  font-size: 24px;
  line-height: 1.25;
  margin-bottom: 8px;
}

.hero-card p {
  font-size: 14px;
  line-height: 1.6;
  opacity: 0.92;
  max-width: 72%;
}

.avatar {
  width: 54px;
  height: 54px;
  border-radius: 18px;
  background: rgba(255,255,255,0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 800;
  flex-shrink: 0;
}

.quick-actions {
  margin: -18px 16px 0;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.action-card {
  min-height: 76px;
  border: 0;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(25, 74, 150, 0.08);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  gap: 8px;
  padding: 14px 15px;
  color: var(--ink-strong);
  font-size: 14px;
  font-weight: 700;
}

.action-card.primary {
  color: #fff;
  background: linear-gradient(135deg, var(--medical-blue), var(--medical-blue-dark));
}

.action-symbol {
  width: 28px;
  height: 28px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.16);
  font-size: 16px;
}

.patient-content {
  padding-top: 18px;
}

.patient-section-title {
  padding: 0 16px 10px;
  font-size: 15px;
  font-weight: 800;
  color: var(--ink-strong);
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  padding: 0 16px;
}

.feature-card {
  min-height: 84px;
  border: 0;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(25, 74, 150, 0.08);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  gap: 8px;
  padding: 14px 15px;
  color: var(--ink-strong);
  font-size: 14px;
  font-weight: 700;
}

.feature-icon {
  width: 28px;
  height: 28px;
  border-radius: 999px;
  background: var(--medical-blue-soft);
  color: var(--medical-blue);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 800;
}

.todo-card {
  margin: 0 16px;
  min-height: 58px;
  border: 0;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(25, 74, 150, 0.08);
  padding: 14px 15px;
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--ink-main);
  font-size: 14px;
  font-weight: 700;
}

.todo-dot {
  width: 28px;
  height: 28px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 800;
}

.todo-dot.warning {
  color: var(--medical-orange);
  background: #fff6df;
}

.todo-dot.success {
  color: var(--medical-green);
  background: #e8f8f2;
}

.todo-arrow {
  margin-left: auto;
  color: var(--ink-muted);
  font-size: 22px;
}
</style>
