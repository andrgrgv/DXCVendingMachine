import { createRouter, createWebHistory } from 'vue-router'
import VendingView from '../views/VendingView.vue'
import AdminView from '../views/AdminView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: VendingView
    },
    {
      path: '/admin',
      component: AdminView
    }
  ]
})

export default router