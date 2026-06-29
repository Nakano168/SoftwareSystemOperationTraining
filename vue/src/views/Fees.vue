<template>
  <div class="patient-page">
    <div class="patient-topbar">
      <span @click="$router.push('/patient/home')" class="patient-back">首页</span>
      <span class="patient-title">在线缴费</span>
      <span></span>
    </div>
    <div class="patient-tabs">
      <button class="patient-tab" :class="{ active: tab === '待支付' }" @click="tab='待支付'">待缴费</button>
      <button class="patient-tab" :class="{ active: tab === 'all' }" @click="tab='all'">全部</button>
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
      channels: [
        { value: 'wechat', label: '微信支付', mark: '微' },
        { value: 'alipay', label: '支付宝支付', mark: '支' },
        { value: 'unionpay', label: '银联卡支付', mark: '银' }
      ]
    }
  },
  computed: {
    displayList() {
      return this.tab === '待支付' ? this.allList.filter(f => f.status === '待支付') : this.allList
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
  },
  methods: {
    businessTypeText,
    feeStatusText,
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
