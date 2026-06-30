import { createRouter, createWebHashHistory } from 'vue-router'
import Login from '../views/Login.vue'
import PatientIndex from '../views/PatientIndex.vue'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/patient',
    component: PatientIndex,
    redirect: '/patient/home',
    children: [
      {
        path: '/patient/home',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: '/patient/appointment',
        name: 'Appointment',
        component: () => import('@/views/Appointment.vue')
      },
      {
        path: '/patient/my-appointments',
        name: 'MyAppointments',
        component: () => import('@/views/MyAppointments.vue')
      },
      {
        path: '/patient/medical-records',
        name: 'MedicalRecords',
        component: () => import('@/views/MedicalRecords.vue')
      },
      {
        path: '/patient/prescriptions',
        name: 'Prescriptions',
        component: () => import('@/views/Prescriptions.vue')
      },
      {
        path: '/patient/exam-reports',
        name: 'ExamReports',
        component: () => import('@/views/ExamReports.vue')
      },
      {
        path: '/patient/ai-center',
        name: 'AiCenter',
        component: () => import('@/views/AiCenter.vue')
      },
      {
        path: '/patient/fees',
        name: 'Fees',
        component: () => import('@/views/Fees.vue')
      },
      {
        path: '/patient/payment-processing',
        name: 'PaymentProcessing',
        component: () => import('@/views/PaymentProcessing.vue')
      },
      {
        path: '/patient/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue')
      },
      {
        path: '/patient/change-password',
        name: 'ChangePassword',
        component: () => import('@/views/ChangePassword.vue')
      },
      {
        path: '/patient/medical-record-detail/:id',
        name: 'MedicalRecordDetail',
        component: () => import('@/views/MedicalRecordDetail.vue')
      },
      {
        path: '/patient/prescription-detail/:id',
        name: 'PrescriptionDetail',
        component: () => import('@/views/PrescriptionDetail.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userId = sessionStorage.getItem('userId')
  if (to.path === '/') {
    if (userId) {
      next('/patient/home')
    } else {
      next()
    }
  } else if (to.path.startsWith('/patient')) {
    if (userId) {
      next()
    } else {
      next('/')
    }
  } else {
    next()
  }
})

export default router
