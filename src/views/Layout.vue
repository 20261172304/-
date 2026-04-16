<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <el-icon><School /></el-icon>
        <span>社团管理系统</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="transparent"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/"><el-icon><HomeFilled /></el-icon><span>首页</span></el-menu-item>
        <el-menu-item index="/clubs"><el-icon><OfficeBuilding /></el-icon><span>社团广场</span></el-menu-item>
        <el-menu-item index="/activities"><el-icon><Calendar /></el-icon><span>活动中心</span></el-menu-item>
        <el-menu-item v-if="!isAdmin && !isClubManager" index="/apply-club">
          <el-icon><Edit /></el-icon><span>申请创建社团</span>
        </el-menu-item>
        <el-menu-item v-if="isClubManager" index="/apply"><el-icon><UserFilled /></el-icon><span>入社审批</span></el-menu-item>
        <el-menu-item v-if="isClubManager" index="/my-clubs"><el-icon><Management /></el-icon><span>我的社团</span></el-menu-item>
        <el-menu-item v-if="isAdmin" index="/club-approve"><el-icon><Checked /></el-icon><span>社团审批</span></el-menu-item>
        <el-menu-item v-if="isAdmin" index="/admin"><el-icon><Setting /></el-icon><span>系统管理</span></el-menu-item>
        <el-menu-item index="/profile"><el-icon><User /></el-icon><span>个人中心</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="user-info">
          <el-avatar 
            :size="36" 
            :src="avatarUrl" 
            class="avatar-clickable"
            @click="goToProfile"
          />
          <div class="user-detail">
            <span class="username">{{ userInfo.username }}</span>
            <el-tag size="small" :type="userRoleType">
              {{ userRoleText }}
            </el-tag>
          </div>
          <el-badge :value="unreadCount" :hidden="unreadCount===0" class="notice-badge">
            <el-button :icon="Bell" circle @click="goToNotices" />
          </el-badge>
        </div>
        <el-button size="small" type="danger" plain @click="handleLogout">退出登录</el-button>
      </el-header>
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { 
  HomeFilled, Calendar, UserFilled, User, Bell, 
  Management, Setting, Edit, Checked, School, OfficeBuilding 
} from '@element-plus/icons-vue'
import { noticeApi } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const instance = getCurrentInstance()
const bus = instance.proxy.$bus

const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const unreadCount = ref(0)
let pollTimer = null

const isAdmin = computed(() => userInfo.value.role === 'ADMIN')
const isClubManager = computed(() => (userInfo.value.managedClubIds || []).length > 0)

const userRoleText = computed(() => {
  if (userInfo.value.role === 'ADMIN') return '超级管理员'
  const managedClubs = userInfo.value.managedClubIds || []
  if (managedClubs.length > 0) return '社团管理员'
  return '普通用户'
})
const userRoleType = computed(() => {
  if (userInfo.value.role === 'ADMIN') return 'danger'
  const managedClubs = userInfo.value.managedClubIds || []
  if (managedClubs.length > 0) return 'warning'
  return 'success'
})

const avatarUrl = computed(() => {
  let avatar = userInfo.value.avatar
  if (!avatar) return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  if (avatar.startsWith('http')) return avatar
  if (avatar.startsWith('/uploads/')) return `http://localhost:8080${avatar}`
  return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
})

let lastUnreadCount = 0
const fetchUnreadCount = async () => {
  const userId = userInfo.value.id
  if (!userId) return
  try {
    const count = await noticeApi.unreadCount(userId)
    if (count > lastUnreadCount && lastUnreadCount !== 0 && router.currentRoute.value.path !== '/notices') {
      ElMessage.info(`您有 ${count - lastUnreadCount} 条新通知`)
    }
    lastUnreadCount = count
    unreadCount.value = count
  } catch (e) {}
}

const goToProfile = () => router.push('/profile')
const goToNotices = () => router.push('/notices')
const handleLogout = () => {
  localStorage.clear()
  router.push('/login')
}

const updateUserInfo = () => {
  userInfo.value = JSON.parse(localStorage.getItem('user') || '{}')
}

onMounted(() => {
  fetchUnreadCount()
  pollTimer = setInterval(fetchUnreadCount, 5000)
  bus.on('avatar-updated', updateUserInfo)
})
onBeforeUnmount(() => {
  if (pollTimer) clearInterval(pollTimer)
  bus.off('avatar-updated', updateUserInfo)
})
</script>



<style scoped>
.layout-container {
  height: 100vh;
  background: #f0f2f6;
}
.sidebar {
  background: linear-gradient(180deg, #1f2d3d 0%, #1a2632 100%);
  box-shadow: 2px 0 12px rgba(0,0,0,0.1);
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  margin-bottom: 20px;
}
.header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-detail {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}
.username {
  font-weight: 500;
}
.main-content {
  padding: 24px;
  background: #f0f2f6;
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
.avatar-clickable {
  cursor: pointer;
  transition: opacity 0.2s;
}
.avatar-clickable:hover {
  opacity: 0.8;
}
</style>