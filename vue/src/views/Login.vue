<template>
  <div class="login-shell">
    <div class="login-screen">
      <div class="status-bar">
        <span>9:41</span>
        <span>5G 92%</span>
      </div>

      <div class="login-hero">
        <div class="brand-mark">＋</div>
        <h1>智慧医疗</h1>
        <p>患者服务平台</p>
      </div>

      <div class="login-panel">
        <div class="tabs">
          <button class="tab" :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">登录</button>
          <button class="tab" :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">注册</button>
        </div>

        <div class="form-scroll">
          <div v-if="activeTab === 'login'" class="form-container">
            <label class="form-item">
              <span>用户名</span>
              <input v-model.trim="loginForm.username" type="text" placeholder="请输入用户名" class="form-input">
            </label>
            <label class="form-item">
              <span>密码</span>
              <input v-model="loginForm.password" type="password" placeholder="请输入密码" class="form-input">
            </label>
            <button class="patient-button submit-btn" @click="handleLogin">登录</button>
          </div>

          <div v-else class="form-container">
            <label class="form-item">
              <span>用户名</span>
              <input v-model.trim="registerForm.username" type="text" placeholder="设置登录用户名" class="form-input">
            </label>
            <label class="form-item">
              <span>密码</span>
              <input v-model="registerForm.password" type="password" placeholder="设置登录密码" class="form-input">
            </label>
            <label class="form-item">
              <span>真实姓名</span>
              <input v-model.trim="registerForm.realName" type="text" placeholder="请输入真实姓名" class="form-input">
            </label>
            <label class="form-item">
              <span>手机号</span>
              <input v-model.trim="registerForm.phone" type="text" placeholder="请输入手机号" class="form-input">
            </label>
            <label class="form-item">
              <span>身份证号</span>
              <input v-model.trim="registerForm.idCard" type="text" placeholder="请输入身份证号" class="form-input">
            </label>
            <div class="gender-row">
              <span>性别</span>
              <label><input type="radio" v-model="registerForm.gender" value="男" /> 男</label>
              <label><input type="radio" v-model="registerForm.gender" value="女" /> 女</label>
            </div>
            <button class="patient-button submit-btn" @click="handleRegister">注册</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import feedback from '@/utils/feedback'

const API = '/api'

export default {
  data() {
    return {
      activeTab: 'login',
      loginForm: { username: '', password: '' },
      registerForm: { username: '', password: '', realName: '', phone: '', idCard: '', gender: '男' }
    }
  },
  methods: {
    async handleLogin() {
      try {
        const res = await axios.post(API + '/patient/login', this.loginForm)
        if (res.data.success) {
          sessionStorage.setItem('userId', res.data.data.userId)
          sessionStorage.setItem('username', res.data.data.username)
          sessionStorage.setItem('realName', res.data.data.realName)
          this.$router.push('/patient/home')
        } else {
          feedback.toast(res.data.message || '登录失败')
        }
      } catch (e) {
        feedback.toast(e.response?.data?.message || '登录失败，请稍后重试')
      }
    },
    async handleRegister() {
      try {
        const res = await axios.post(API + '/patient/register', this.registerForm)
        if (res.data.success) {
          feedback.toast(res.data.message)
          this.activeTab = 'login'
        } else {
          feedback.toast(res.data.message || '注册失败')
        }
      } catch (e) {
        feedback.toast(e.response?.data?.message || '注册失败，请稍后重试')
      }
    }
  }
}
</script>

<style scoped>
.login-shell {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 18px;
  background:
    radial-gradient(circle at 20% 10%, rgba(47, 128, 209, 0.18), transparent 28%),
    linear-gradient(135deg, #eef7ff 0%, #f7fbff 48%, #e9f4ff 100%);
}

.login-screen {
  width: min(390px, 100vw - 28px);
  height: min(844px, 100vh - 36px);
  min-height: 720px;
  background: var(--page-bg);
  border-radius: 32px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(42, 96, 148, 0.22);
  display: flex;
  flex-direction: column;
}

.status-bar {
  display: flex;
  justify-content: space-between;
  padding: 12px 24px 8px;
  font-size: 12px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.86);
  background: var(--medical-blue);
  flex: 0 0 auto;
}

.login-hero {
  position: relative;
  padding: 34px 24px 78px;
  color: #fff;
  text-align: center;
  background:
    linear-gradient(135deg, var(--medical-blue), var(--medical-blue-dark));
  flex: 0 0 auto;
}

.login-hero::after {
  content: "";
  position: absolute;
  left: -10%;
  right: -10%;
  bottom: -42px;
  height: 76px;
  background: var(--page-bg);
  border-radius: 50% 50% 0 0;
}

.brand-mark {
  width: 70px;
  height: 70px;
  border-radius: 22px;
  margin: 0 auto 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  font-size: 34px;
  font-weight: 300;
}

.login-hero h1 {
  font-size: 28px;
  letter-spacing: 0;
  margin-bottom: 6px;
}

.login-hero p {
  font-size: 14px;
  opacity: 0.86;
}

.login-panel {
  position: relative;
  z-index: 1;
  margin: -44px 18px 20px;
  padding: 18px;
  min-height: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 18px;
  box-shadow: var(--shadow-soft);
  overflow: hidden;
}

.tabs {
  flex: 0 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  padding: 4px;
  margin-bottom: 14px;
  background: #eef5fb;
  border-radius: 14px;
}

.tab {
  height: 38px;
  border: 0;
  border-radius: 11px;
  background: transparent;
  color: var(--ink-muted);
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
}

.tab.active {
  color: var(--medical-blue);
  background: #fff;
  box-shadow: 0 6px 14px rgba(44, 109, 169, 0.12);
}

.form-scroll {
  min-height: 0;
  flex: 1;
  overflow-y: auto;
  padding-right: 2px;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.form-scroll::-webkit-scrollbar {
  display: none;
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 13px;
  padding-bottom: 4px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 7px;
  font-size: 13px;
  color: var(--ink-muted);
  font-weight: 700;
}

.form-input {
  width: 100%;
  height: 44px;
  padding: 0 13px;
  border: 1px solid var(--line-soft);
  border-radius: 12px;
  background: #fbfdff;
  font-size: 15px;
  color: var(--ink-main);
  outline: none;
}

.form-input:focus {
  border-color: var(--medical-blue);
  box-shadow: 0 0 0 3px rgba(47, 128, 209, 0.12);
}

.gender-row {
  display: flex;
  align-items: center;
  gap: 18px;
  min-height: 36px;
  font-size: 14px;
  color: var(--ink-main);
}

.gender-row > span {
  color: var(--ink-muted);
  font-weight: 700;
}

.gender-row label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.submit-btn {
  width: 100%;
  margin-top: 4px;
}
</style>
