<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in stats" :key="stat.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ color: stat.color }">
            <el-icon :size="40"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-title">{{ stat.title }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :xs="24" :md="12">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>🔥 热门社团推荐</span>
              <el-button text type="primary" @click="goToClubs">查看更多</el-button>
            </div>
          </template>
          <el-table :data="recommendClubs" stripe>
            <el-table-column prop="name" label="社团名称" />
            <el-table-column prop="leader" label="社长" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button size="small" text @click="viewClub(row.id)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>📅 近期热门活动</span>
              <el-button text type="primary" @click="goToActivities">查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentActivities" stripe>
            <el-table-column prop="title" label="活动名称" />
            <el-table-column prop="time" label="活动时间" width="160" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button size="small" text @click="viewActivity(row.id)">报名</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="quick-links">
      <template #header>
        <span>⚡ 快速入口</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6" v-for="link in quickLinks" :key="link.path">
          <div class="quick-item" @click="$router.push(link.path)">
            <el-icon :size="32"><component :is="link.icon" /></el-icon>
            <span>{{ link.name }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { clubApi, activityApi, noticeApi } from '../api'
import { OfficeBuilding, Calendar, User, Bell } from '@element-plus/icons-vue'

const router = useRouter()
const stats = ref([
  { title: '社团总数', value: 0, icon: 'OfficeBuilding', color: '#409eff' },
  { title: '活动总数', value: 0, icon: 'Calendar', color: '#67c23a' },
  { title: '成员总数', value: 0, icon: 'User', color: '#e6a23c' },
  { title: '未读通知', value: 0, icon: 'Bell', color: '#f56c6c' }
])
const recommendClubs = ref([])
const recentActivities = ref([])
const quickLinks = [
  { name: '社团广场', path: '/clubs', icon: 'OfficeBuilding' },
  { name: '活动中心', path: '/activities', icon: 'Calendar' },
  { name: '入社审批', path: '/apply', icon: 'User' },
  { name: '个人中心', path: '/profile', icon: 'User' }
]

const fetchStats = async () => {
  try {
    const clubs = await clubApi.list({})
    stats.value[0].value = clubs.length
    const activities = await activityApi.list({})
    stats.value[1].value = activities.length
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (user.id) {
      const unread = await noticeApi.unreadCount(user.id)
      stats.value[3].value = unread
    }
  } catch (e) {}
}
const fetchRecommendClubs = async () => {
  const clubs = await clubApi.list({})
  recommendClubs.value = clubs.slice(0, 5)
}
const fetchRecentActivities = async () => {
  const activities = await activityApi.list({})
  const now = new Date()
  recentActivities.value = activities.filter(a => new Date(a.time) > now).slice(0, 5)
}
const goToClubs = () => router.push('/clubs')
const goToActivities = () => router.push('/activities')
const viewClub = (id) => router.push({ name: 'ClubDetail', params: { id } })
const viewActivity = (id) => router.push({ path: '/activities', query: { clubId: id } })

onMounted(() => {
  fetchStats()
  fetchRecommendClubs()
  fetchRecentActivities()
})
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
  margin: 0 auto;
}
.stats-row {
  margin-bottom: 24px;
}
.stat-card {
  display: flex;
  align-items: center;
  padding: 16px;
  border-radius: 16px;
}
.stat-icon {
  margin-right: 16px;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  line-height: 1.2;
}
.stat-title {
  color: #909399;
  font-size: 14px;
  margin-top: 4px;
}
.section-card {
  border-radius: 16px;
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.quick-links {
  border-radius: 16px;
}
.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 12px;
}
.quick-item:hover {
  background: #f5f7fa;
  transform: translateY(-2px);
}
.quick-item span {
  margin-top: 8px;
  font-size: 14px;
}
</style>