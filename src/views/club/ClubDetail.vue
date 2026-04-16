<template>
  <div class="club-detail">
    <el-card class="club-header" shadow="never">
      <div class="club-header-content">
        <div class="club-logo">
          <el-avatar :size="80" :src="clubLogoUrl" />
          <el-button v-if="isManager || isAdmin" size="small" plain @click="uploadLogo" style="margin-top: 8px">更换标志</el-button>
          <input type="file" ref="logoInput" style="display:none" accept="image/jpeg,image/png" @change="handleLogoChange" />
        </div>
        <div class="club-title">
          <h1>{{ club.name }}</h1>
          <el-tag :type="club.status === 'APPROVED' ? 'success' : 'warning'">
            {{ club.status === 'APPROVED' ? '已认证' : '待审核' }}
          </el-tag>
          <el-button 
            v-if="isManager || isAdmin" 
            type="danger" 
            plain 
            size="small" 
            @click="disableClub"
            style="margin-left: 12px"
          >废除社团</el-button>
        </div>
      </div>
      <p class="desc">{{ club.description || '暂无简介' }}</p>
      <div class="actions">
        <el-button v-if="!isMember && userInfo.id" type="primary" @click="applyJoin" :loading="applying">申请加入</el-button>
      </div>
    </el-card>

    <el-tabs v-model="activeTab" class="club-tabs" type="border-card">
      <!-- 公告标签页 -->
      <el-tab-pane name="notice">
        <template #label>
          <span><el-icon><Bell /></el-icon> 社团公告</span>
        </template>
        <div class="notice-header" v-if="isManager">
          <el-button type="primary" plain @click="dialogVisible = true">发布公告</el-button>
        </div>
        <el-table :data="notices" stripe>
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="content" label="内容" show-overflow-tooltip />
          <el-table-column prop="createTime" label="发布时间" width="180" />
          <el-table-column v-if="isManager" label="操作" width="100">
            <template #default="scope">
              <el-button size="small" text @click="toggleTop(scope.row)">{{ scope.row.isTop ? '取消置顶' : '置顶' }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 成员标签页 -->
      <el-tab-pane name="member">
        <template #label>
          <span><el-icon><User /></el-icon> 成员列表</span>
        </template>
        <el-table :data="members" stripe>
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="clubRole" label="角色" />
          <el-table-column v-if="isManager" label="操作" width="180">
            <template #default="scope">
              <el-button size="small" text v-if="scope.row.clubRole !== 'MANAGER'" @click="setRole(scope.row, 'MANAGER')">设为管理员</el-button>
              <el-button size="small" type="danger" text @click="removeMember(scope.row)">踢出</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 活动标签页 -->
      <el-tab-pane name="activity">
        <template #label>
          <span><el-icon><Calendar /></el-icon> 社团活动</span>
        </template>
        <div class="activity-header" v-if="isManager">
          <el-button type="primary" plain @click="showAddActivity = true">发布活动</el-button>
        </div>
        <el-table :data="activities" stripe>
          <el-table-column prop="title" label="活动标题" />
          <el-table-column label="活动时间" width="220">
            <template #default="{ row }">
              {{ formatDate(row.time) }} ~ {{ formatDate(row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="currentParticipants" label="已报/总人数" width="120">
            <template #default="{ row }">
              <el-progress :percentage="Math.round(row.currentParticipants / row.maxParticipants * 100)" :format="() => `${row.currentParticipants}/${row.maxParticipants}`" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button size="small" text type="primary" @click="viewSignList(scope.row)">报名名单</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 发布公告弹窗 -->
    <el-dialog v-model="dialogVisible" title="发布公告" width="500px">
      <el-form :model="noticeForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="noticeForm.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" v-model="noticeForm.content" :rows="4" />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="noticeForm.isTop" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="publishNotice">发布</el-button>
      </template>
    </el-dialog>

    <!-- 发布活动弹窗 -->
    <el-dialog v-model="showAddActivity" title="发布活动" width="500px">
      <el-form :model="activityForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="activityForm.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" v-model="activityForm.content" rows="3" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="activityForm.time" type="datetime" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="activityForm.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="人数限制">
          <el-input-number v-model="activityForm.maxParticipants" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddActivity = false">取消</el-button>
        <el-button type="primary" @click="publishActivity">发布</el-button>
      </template>
    </el-dialog>

    <!-- 报名名单弹窗 -->
    <el-dialog v-model="signListDialog" title="报名名单" width="400px">
      <el-table :data="signList" stripe>
        <el-table-column prop="username" label="用户名" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bell, User, Calendar } from '@element-plus/icons-vue'
import { clubApi, clubNoticeApi, clubMemberApi, activityApi, memberApplyApi } from '../../api'
import request from '../../api'

const route = useRoute()
const router = useRouter()
const clubId = parseInt(route.params.id)

const club = ref({})
const notices = ref([])
const members = ref([])
const activities = ref([])
const applying = ref(false)
const activeTab = ref('notice')
const dialogVisible = ref(false)
const noticeForm = ref({ title: '', content: '', isTop: false })
const showAddActivity = ref(false)
// 活动表单，添加 endTime 字段
const activityForm = ref({ 
  title: '', 
  content: '', 
  time: '', 
  endTime: null, 
  maxParticipants: 10 
})
const signListDialog = ref(false)
const signList = ref([])
const logoInput = ref(null)
const defaultClubLogo = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
const isAdmin = computed(() => userInfo.role === 'ADMIN')
const isManager = ref(false)
const isMember = ref(false)

// 计算完整的社团标志URL
const clubLogoUrl = computed(() => {
  const logo = club.value.logo
  if (!logo) return defaultClubLogo
  if (logo.startsWith('http')) return logo
  if (logo.startsWith('/uploads/')) return `http://localhost:8080${logo}`
  return defaultClubLogo
})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '待定'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${date.getMonth()+1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`
}

const loadClub = async () => {
  const list = await clubApi.list({})
  club.value = list.find(c => c.id == clubId) || {}
}

const loadNotices = async () => {
  try {
    const res = await clubNoticeApi.list(clubId)
    notices.value = res
  } catch (error) {
    console.error('加载公告失败', error)
  }
}
const loadMembers = async () => {
  members.value = await clubMemberApi.list(clubId)
  isManager.value = members.value.some(m => m.id === userInfo.id && m.clubRole === 'MANAGER')
  isMember.value = members.value.some(m => m.id === userInfo.id)
}

const loadActivities = async () => {
  activities.value = await activityApi.list({ clubId })
}

const publishNotice = async () => {
  await clubNoticeApi.add({ ...noticeForm.value, clubId })
  ElMessage.success('发布成功')
  dialogVisible.value = false
  loadNotices()
}

const toggleTop = async (row) => {
  await clubNoticeApi.setTop(row.id, !row.isTop)
  loadNotices()
}

const setRole = async (member, role) => {
  await clubMemberApi.setRole(clubId, member.id, role)
  loadMembers()
}

const removeMember = async (member) => {
  await clubMemberApi.remove(clubId, member.id)
  loadMembers()
}

const publishActivity = async () => {
  await activityApi.add({ ...activityForm.value, clubId })
  ElMessage.success('活动发布成功')
  showAddActivity.value = false
  loadActivities()
}

const viewSignList = async (activity) => {
  signList.value = await activityApi.signList(activity.id)
  signListDialog.value = true
}

const applyJoin = async () => {
  applying.value = true
  try {
    const res = await memberApplyApi.add({ clubId, reason: '我想加入' })
    ElMessage.success(res)
    await loadMembers()
  } catch (e) {
    ElMessage.error('申请失败')
  } finally {
    applying.value = false
  }
}

const uploadLogo = () => logoInput.value.click()
const handleLogoChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await clubApi.uploadLogo(clubId, file)
    if (res.code === 200) {
      club.value.logo = res.url
      ElMessage.success('社团标志更新成功')
    } else {
      ElMessage.error(res.msg || '上传失败')
    }
  } catch {
    ElMessage.error('上传失败')
  }
}

const disableClub = async () => {
  try {
    await ElMessageBox.confirm(
      '废除社团后，该社团将不再显示在列表中，所有活动也将无法报名。是否确认废除？',
      '警告',
      {
        confirmButtonText: '确认废除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const res = await request.put(`/club/disable/${clubId}`)
    if (res.code === 200) {
      ElMessage.success('社团已废除')
      router.push('/clubs')
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadClub()
  loadNotices()
  loadMembers()
  loadActivities()
})
</script>

<style scoped>
.club-detail {
  max-width: 1200px;
  margin: 0 auto;
}
.club-header {
  margin-bottom: 24px;
  border-radius: 16px;
}
.club-header-content {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}
.club-logo {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.club-title {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.club-title h1 {
  margin: 0;
  font-size: 28px;
}
.desc {
  color: #606266;
  line-height: 1.6;
  margin: 12px 0;
}
.actions {
  margin-top: 16px;
}
.club-tabs {
  border-radius: 16px;
  overflow: hidden;
}
.notice-header, .activity-header {
  margin-bottom: 16px;
  text-align: right;
}
</style>