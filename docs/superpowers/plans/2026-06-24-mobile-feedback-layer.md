# Mobile Feedback Layer Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace browser alert/confirm dialogs in the patient frontend with a centered mobile-style toast and a matching custom confirm dialog.

**Architecture:** Add one app-wide feedback layer that renders above every patient page and exposes two small methods: `toast(message, type)` for success/error/info hints and `confirm(message)` for blocking decisions. Keep the existing page logic intact except for swapping native dialogs to the new layer so the change is easy to revert.

**Tech Stack:** Vue 3, existing Element Plus app shell, scoped CSS, simple reactive state.

---

### Task 1: Add the global feedback layer

**Files:**
- Create: `vue/src/components/FeedbackLayer.vue`
- Modify: `vue/src/App.vue`

- [ ] **Step 1: Create the feedback component**

```vue
<template>
  <teleport to="body">
    <div v-if="toast.visible" class="feedback-toast" :class="toast.type">
      {{ toast.message }}
    </div>
    <div v-if="confirm.visible" class="feedback-mask">
      <div class="feedback-dialog">
        <div class="feedback-dialog__message">{{ confirm.message }}</div>
        <div class="feedback-dialog__actions">
          <button class="feedback-btn secondary" @click="resolve(false)">取消</button>
          <button class="feedback-btn primary" @click="resolve(true)">确定</button>
        </div>
      </div>
    </div>
  </teleport>
</template>
```

- [ ] **Step 2: Wire the layer into the app shell**

```vue
<template>
  <router-view />
  <FeedbackLayer ref="feedbackLayer" />
</template>
```

- [ ] **Step 3: Verify the component can animate in and out**

Run: open any patient page after adding sample state, confirm the toast sits centered and fades out without a browser alert.

### Task 2: Expose a tiny feedback API

**Files:**
- Modify: `vue/src/App.vue`
- Create: `vue/src/utils/feedback.js`

- [ ] **Step 1: Add a shared controller**

```js
let api = null

export function setFeedbackApi(nextApi) {
  api = nextApi
}

export function toast(message, type = 'info') {
  return api?.toast(message, type)
}

export function confirm(message) {
  return api?.confirm(message) ?? Promise.resolve(false)
}
```

- [ ] **Step 2: Register the controller from the root app**

```js
onMounted(() => {
  setFeedbackApi(feedbackLayerRef.value)
})
```

- [ ] **Step 3: Verify the API is available before any page code runs**

Run: reload the app and confirm the first toast request does not throw.

### Task 3: Replace native dialogs on the patient pages

**Files:**
- Modify: `vue/src/views/Login.vue`
- Modify: `vue/src/views/Appointment.vue`
- Modify: `vue/src/views/MyAppointments.vue`
- Modify: `vue/src/views/PaymentProcessing.vue`

- [ ] **Step 1: Replace success and failure alerts with `toast(...)`**

```js
import { toast } from '@/utils/feedback'

toast('挂号成功，请按时就诊', 'success')
toast(res.data.message || '登录失败', 'error')
```

- [ ] **Step 2: Replace the cancel confirmation with `await confirm(...)`**

```js
if (!(await confirm('确定取消挂号？'))) return
```

- [ ] **Step 3: Keep every branching outcome unchanged**

Use the same backend calls and route changes as before; only the user-visible prompt changes.

### Task 4: Style the mobile feedback UI

**Files:**
- Modify: `vue/src/assets/patient-theme.css`
- Modify: `vue/src/components/FeedbackLayer.vue`

- [ ] **Step 1: Add centered toast styles and fade animations**

```css
.feedback-toast {
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  opacity: 0;
  animation: feedback-fade 2s ease forwards;
}
```

- [ ] **Step 2: Add a simple confirm mask and dialog**

```css
.feedback-mask { position: fixed; inset: 0; }
.feedback-dialog { position: fixed; left: 50%; top: 50%; transform: translate(-50%, -50%); }
```

- [ ] **Step 3: Verify the dialog matches the mobile layout**

Run: check on a phone-sized viewport that the prompt stays centered and does not cover the navigation bar.

### Task 5: Remove remaining native dialogs and verify

**Files:**
- Modify: the same four view files above, if any `alert` or `confirm` remain

- [ ] **Step 1: Search for leftover browser dialogs**

Run: `rg -n "alert\\(|confirm\\(" .\\vue\\src`

Expected: no matches in the patient frontend views.

- [ ] **Step 2: Smoke test the main flows**

Run through login, appointment booking, cancellation, and payment flows once.

- [ ] **Step 3: Commit the UI change**

```bash
git add vue/src/components/FeedbackLayer.vue vue/src/utils/feedback.js vue/src/App.vue vue/src/views/Login.vue vue/src/views/Appointment.vue vue/src/views/MyAppointments.vue vue/src/views/PaymentProcessing.vue vue/src/assets/patient-theme.css
git commit -m "feat: replace native dialogs with mobile feedback layer"
```
