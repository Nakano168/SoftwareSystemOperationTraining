<template>
  <div class="patient-page">
    <div class="patient-topbar">
      <span @click="$router.push('/patient/home')" class="patient-back">首页</span>
      <span class="patient-title">在线缴费</span>
      <span></span>
    </div>
    <div class="filter-shell">
      <button class="filter-trigger" type="button" @click="toggleFilterPanel">
        <span class="filter-trigger-label">缴费状态</span>
        <span class="filter-trigger-value">{{ currentTabLabel }}</span>
        <span class="filter-trigger-arrow" :class="{ open: filterPanelOpen }"></span>
      </button>

      <transition name="panel-fade">
        <div v-if="filterPanelOpen" class="filter-panel">
          <button
            v-for="option in filterOptions"
            :key="option.value"
            type="button"
            class="filter-option"
            :class="{ active: tab === option.value }"
            @click="selectTab(option.value)"
          >
            <span class="filter-option-title">{{ option.label }}</span>
            <span class="filter-option-count">{{ option.count }}</span>
          </button>
        </div>
      </transition>
    </div>
    <div class="patient-content fee-list">
      <div class="fee-card patient-card" v-for="f in displayList" :key="f.feeOrderId">
        <div class="card-head">
          <span class="date">{{ f.createdAt?.substring(0,10) }}</span>
          <span class="patient-status" :class="feeStatusClass(f.status)">{{ feeStatusText(f.status) }}</span>
        </div>
        <div class="patient-row"><span class="label">单号</span><span class="value">{{ f.orderNo }}</span></div>
        <div class="patient-row"><span class="label">类型</span><span class="value">{{ businessTypeText(f.businessType) }}</span></div>
        <div class="amount-row">
          <span>应付金额</span>
          <strong>¥{{ f.totalAmount }}</strong>
        </div>
        <div class="payment-channels" v-if="f.status === '待支付'">
          <button
            v-for="channel in channels"
            :key="channel.value"
            class="pay-channel"
            :class="channel.value"
            @click="goPay(f, channel)"
          >
            <span class="channel-mark">{{ channel.mark }}</span>
            <span>{{ channel.label }}</span>
          </button>
        </div>
      </div>
      <div v-if="displayList.length === 0" class="patient-empty">暂无记录</div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { businessTypeText, feeStatusText } from '@/utils/statusLabels'
export default {
  data() {
    return {
      tab: '待支付',
      allList: [],
      patientId: null,
      filterPanelOpen: false,
      channels: [
        { value: 'wechat', label: '微信支付', mark: '微' },
        { value: 'alipay', label: '支付宝支付', mark: '支' },
        { value: 'unionpay', label: '银联卡支付', mark: '银' }
      ]
    }
  },
  computed: {
    filterOptions() {
      const labels = [
        { value: 'all', label: '全部' },
        { value: '待支付', label: '待支付' },
        { value: '已支付', label: '已支付' },
        { value: '已退费', label: '已退费' }
      ]
      return labels.map(item => ({
        ...item,
        count: item.value === 'all'
          ? this.allList.length
          : this.allList.filter(f => f.status === item.value).length
      }))
    },
    currentTabLabel() {
      const active = this.filterOptions.find(item => item.value === this.tab)
      return active ? `${active.label}${active.value === 'all' ? '' : ` · ${active.count}`}` : '待支付'
    },
    displayList() {
      if (this.tab === 'all') return this.allList
      return this.allList.filter(f => f.status === this.tab)
    }
  },
  async mounted() {
    const userId = sessionStorage.getItem('userId')
    if (!userId) return
    const res = await axios.get(`/api/patient/info/${userId}`)
    if (res.data.success && res.data.data) {
      this.patientId = res.data.data.patientId
      const r2 = await axios.get(`/api/fee/my/${this.patientId}`)
      if (r2.data.success) this.allList = r2.data.data
    }
    document.addEventListener('click', this.handleDocumentClick)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleDocumentClick)
  },
  methods: {
    businessTypeText,
    feeStatusText,
    toggleFilterPanel() {
      this.filterPanelOpen = !this.filterPanelOpen
    },
    selectTab(value) {
      this.tab = value
      this.filterPanelOpen = false
    },
    handleDocumentClick(event) {
      const shell = this.$el?.querySelector('.filter-shell')
      if (!shell) return
      if (!shell.contains(event.target)) {
        this.filterPanelOpen = false
      }
    },
    feeStatusClass(status) {
      if (status === '待支付' || status === 'unpaid') return 'warning'
      if (status === '已支付' || status === 'paid') return 'success'
      if (status === '已退费' || status === 'refunded') return 'info'
      return 'warning'
    },
    goPay(order, channel) {
      this.$router.push({
        path: '/patient/payment-processing',
        query: {
          feeOrderId: order.feeOrderId,
          channel: channel.value,
          channelName: channel.label,
          amount: order.totalAmount
        }
      })
    }
  }
}
</script>

<style scoped>
.fee-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filter-shell {
  position: relative;
  margin-bottom: 12px;
}

.filter-trigger {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid var(--line-soft);
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  cursor: pointer;
}

.filter-trigger-label {
  font-size: 12px;
  color: var(--ink-muted);
  flex: 0 0 auto;
}

.filter-trigger-value {
  margin-left: auto;
  color: var(--ink-strong);
  font-weight: 800;
}

.filter-trigger-arrow {
  width: 10px;
  height: 10px;
  border-right: 2px solid var(--ink-muted);
  border-bottom: 2px solid var(--ink-muted);
  transform: rotate(45deg);
  transition: transform 0.2s ease;
  margin-left: 4px;
}

.filter-trigger-arrow.open {
  transform: rotate(-135deg);
}

.filter-panel {
  position: absolute;
  top: calc(100% + 10px);
  left: 0;
  right: 0;
  z-index: 20;
  padding: 12px;
  border: 1px solid var(--line-soft);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12);
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.filter-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 12px 14px;
  border: 1px solid var(--line-soft);
  border-radius: 12px;
  background: #fff;
  cursor: pointer;
  text-align: left;
}

.filter-option-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--ink-strong);
}

.filter-option-count {
  min-width: 28px;
  padding: 2px 8px;
  border-radius: 999px;
  background: #f2f6ff;
  color: var(--medical-blue);
  font-size: 12px;
  font-weight: 700;
  text-align: center;
}

.filter-option.active {
  border-color: var(--medical-blue);
  background: linear-gradient(180deg, rgba(48, 118, 255, 0.08), rgba(48, 118, 255, 0.03));
  box-shadow: 0 8px 18px rgba(48, 118, 255, 0.12);
}

.filter-option.active .filter-option-title {
  color: var(--medical-blue);
}

.filter-option.active .filter-option-count {
  background: rgba(48, 118, 255, 0.14);
  color: var(--medical-blue);
}

.panel-fade-enter-active,
.panel-fade-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.panel-fade-enter-from,
.panel-fade-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

.fee-card {
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

.amount-row {
  margin-top: 10px;
  padding: 12px;
  border-radius: 12px;
  background: #f7fbff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--ink-muted);
  font-size: 13px;
}

.amount-row strong {
  color: var(--medical-red);
  font-size: 20px;
}

.payment-channels {
  margin-top: 12px;
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

.pay-channel {
  width: 100%;
  min-height: 46px;
  border: 1px solid var(--line-soft);
  border-radius: 12px;
  background: #fff;
  color: var(--ink-main);
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 12px;
  cursor: pointer;
}

.channel-mark {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 13px;
  flex: 0 0 auto;
}

.pay-channel.wechat .channel-mark {
  background: var(--medical-green);
}

.pay-channel.alipay .channel-mark {
  background: var(--medical-blue);
}

.pay-channel.unionpay .channel-mark {
  background: var(--medical-red);
}
</style>
