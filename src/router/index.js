import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Layout from '../views/Layout.vue'
import Dashboard from '../views/Dashboard.vue'
import ClubList from '../views/club/ClubList.vue'
import ActivityList from '../views/activity/ActivityList.vue'
import ApplyList from '../views/club/ApplyList.vue'
import UserProfile from '../views/user/UserProfile.vue'
import ClubDetail from '../views/club/ClubDetail.vue'
import NoticeCenter from '../views/NoticeCenter.vue'
import MyClubs from '../views/club/MyClubs.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import ApplyCreateClub from '../views/club/ApplyCreateClub.vue'
import ClubApprove from '../views/admin/ClubApprove.vue'

const routes = [
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/',
    component: Layout,
    children: [
      { path: '', component: Dashboard },
      { path: 'clubs', component: ClubList },
      { path: 'activities', component: ActivityList },
      { path: 'apply', component: ApplyList },
      { path: 'profile', component: UserProfile },
      { path: 'club/:id', component: ClubDetail, name: 'ClubDetail' },
      { path: 'notices', component: NoticeCenter },
      { path: 'my-clubs', component: MyClubs },
      { path: 'admin', component: AdminDashboard },
      { path: 'apply-club', component: ApplyCreateClub },
      { path: 'club-approve', component: ClubApprove }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (!token && to.path !== '/login' && to.path !== '/register') {
    next('/login')
  } else {
    next()
  }
})

export default router