<template>
  <div class="phone-container">
    <div class="phone-screen">
      <div class="status-bar">
        <span>9:41</span>
        <span>5G 92%</span>
      </div>
      <div class="page-content">
        <router-view />
      </div>
      <div class="bottom-nav">
        <button class="nav-item" :class="{ active: curRoute === '/patient/home' }" @click="goTo('/patient/home')">
          <span class="nav-icon">⌂</span>
          <span class="nav-label">首页</span>
        </button>
        <button class="nav-item" :class="{ active: curRoute === '/patient/appointment' }" @click="goTo('/patient/appointment')">
          <span class="nav-icon">＋</span>
          <span class="nav-label">挂号</span>
        </button>
        <button class="nav-item" :class="{ active: curRoute === '/patient/my-appointments' }" @click="goTo('/patient/my-appointments')">
          <span class="nav-icon">≡</span>
          <span class="nav-label">记录</span>
        </button>
        <button class="nav-item" :class="{ active: curRoute === '/patient/fees' }" @click="goTo('/patient/fees')">
          <span class="nav-icon">¥</span>
          <span class="nav-label">费用</span>
        </button>
        <button class="nav-item" :class="{ active: curRoute === '/patient/profile' }" @click="goTo('/patient/profile')">
          <span class="nav-icon">○</span>
          <span class="nav-label">我的</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      curRoute: '/patient/home'
    }
  },
  watch: {
    '$route.path'(val) { this.curRoute = val }
  },
  mounted() { this.curRoute = this.$route.path },
  methods: {
    goTo(path) {
      if (this.curRoute !== path) this.$router.push(path)
    }
  }
}
</script>

<style scoped>
.phone-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 18px;
  background:
    radial-gradient(circle at 20% 10%, rgba(47, 128, 209, 0.16), transparent 28%),
    linear-gradient(135deg, #eef7ff 0%, #f7fbff 48%, #e9f4ff 100%);
}

.phone-screen {
  width: min(390px, 100vw - 28px);
  height: min(844px, 100vh - 36px);
  min-height: 720px;
  background: var(--page-bg);
  border: 1px solid rgba(255, 255, 255, 0.9);
  border-radius: 32px;
  box-shadow: 0 24px 60px rgba(42, 96, 148, 0.22);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  position: relative;
}

.status-bar {
  display: flex;
  justify-content: space-between;
  padding: 12px 24px 8px;
  font-size: 12px;
  font-weight: 700;
  color: var(--ink-muted);
  background: rgba(255, 255, 255, 0.84);
  flex-shrink: 0;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.page-content::-webkit-scrollbar {
  display: none;
}

.bottom-nav {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 4px;
  border-top: 1px solid rgba(221, 234, 247, 0.9);
  background: rgba(255, 255, 255, 0.96);
  padding: 8px 10px 10px;
  flex-shrink: 0;
}

.nav-item {
  border: 0;
  background: transparent;
  min-height: 52px;
  border-radius: 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  color: var(--ink-muted);
  cursor: pointer;
}

.nav-icon {
  width: 24px;
  height: 24px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 17px;
  line-height: 1;
  font-weight: 800;
}

.nav-label {
  font-size: 11px;
  font-weight: 700;
}

.nav-item.active {
  color: var(--medical-blue);
  background: var(--medical-blue-soft);
}

.nav-item.active .nav-icon {
  color: #fff;
  background: linear-gradient(135deg, var(--medical-blue), var(--medical-blue-dark));
}
</style>
