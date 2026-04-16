<template>
  <div class="activity-list">
    <div class="page-header">
      <h2>活动中心</h2>
      <el-radio-group v-model="filterType" @change="loadActivities">
        <el-radio-button value="upcoming">即将开始</el-radio-button>
        <el-radio-button value="signed">曾参与活动</el-radio-button>
      </el-radio-group>
      <el-button type="primary" :icon="Refresh" @click="loadActivities">刷新</el-button>
    </div>
    <el-table :data="activities" stripe v-loading="loading">
      <el-table-column prop="title" label="活动标题" min-width="150" />
      <el-table-column label="活动时间" width="240">
        <template #default="{ row }">
          {{ formatDateTime(row.time) }} ~ {{ formatDateTime(row.endTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="currentParticipants" label="已报/总人数" width="120">
        <template #default="{ row }">
          <el-progress :percentage="Math.round(row.currentParticipants / row.maxParticipants * 100)" :format="() => `${row.currentParticipants}/${row.maxParticipants}`" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="400">
        <template #default="{ row }">
          <!-- 未报名时显示报名按钮 -->
          <el-button v-if="!isSigned(row)" size="small" type="success" plain @click="handleSign(row)">报名</el-button>
          <!-- 已报名时显示取消报名和签到按钮 -->
          <template v-else>
            <el-button size="small" type="warning" plain @click="handleCancel(row)">取消报名</el-button>
            <el-button 
              size="small" 
              type="primary" 
              plain 
              :disabled="isSignedIn(row)"
              @click="openSignDialog(row)"
              :class="{ 'disabled-sign-btn': isSignedIn(row) }"
            >{{ isSignedIn(row) ? '已签到' : '签到' }}</el-button>
          </template>
          <el-button size="small" type="info" plain @click="showSignList(row)">名单</el-button>
          <el-button v-if="isManager" size="small" type="primary" plain @click="generateCode(row)">生成签到码</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 生成签到码弹窗 -->
    <el-dialog v-model="codeDialog" title="活动签到码" width="400px">
      <div class="sign-code">
        <p class="code">{{ signCode }}</p>
        <el-button type="primary" plain @click="copyCode">复制签到码</el-button>
      </div>
    </el-dialog>

    <!-- 成员签到弹窗 -->
    <el-dialog v-model="signDialogVisible" title="活动签到" width="400px">
      <div class="sign-code">
        <p>请输入签到码：</p>
        <el-input v-model="signCodeInput" placeholder="签到码" clearable />
        <el-button type="primary" style="margin-top: 16px" @click="submitSign">确认签到</el-button>
      </div>
    </el-dialog>

    <!-- 报名名单弹窗 -->
    <el-dialog v-model="listDialog" title="报名人员名单" width="500px">
      <el-table :data="signList" stripe>
        <el-table-column prop="username" label="用户名" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { activityApi, clubMemberApi } from '../../api'

const route = useRoute()
const activities = ref([])
const filterType = ref('upcoming')
const loading = ref(false)
const user = JSON.parse(localStorage.getItem('user') || '{}')
const mySigns = ref([])          // 已报名的活动ID列表
const mySignIns = ref([])        // 已签到的活动ID列表
const codeDialog = ref(false)
const signCode = ref('')
const currentActivityId = ref(null)
const listDialog = ref(false)
const signList = ref([])
const signDialogVisible = ref(false)
const signCodeInput = ref('')
const currentSignActivity = ref(null)

// 获取用户管理的社团ID列表
const managedClubIds = user.managedClubIds || []
console.log('用户管理的社团ID:', managedClubIds)

// 确定社团ID：优先URL参数，否则取用户管理的第一个社团（若用户是管理员）
let initialClubId = route.query.clubId ? parseInt(route.query.clubId) : null
if (!initialClubId && managedClubIds.length > 0) {
  initialClubId = managedClubIds[0]
  console.log('自动使用管理的社团ID:', initialClubId)
}
const clubId = ref(initialClubId)

// 判断当前用户是否为当前社团的管理员
const isManager = computed(() => {
  if (!clubId.value) return false
  return managedClubIds.includes(clubId.value)
})

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '待定'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${date.getMonth()+1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`
}

// 获取用户已报名的活动ID列表
const loadUserSigns = async () => {
  if (!user.id) return
  try {
    const res = await activityApi.mySigned({ userId: user.id })
    mySigns.value = (res || []).map(a => a.id)
  } catch {
    mySigns.value = []
  }
}

// 获取用户已签到的活动ID列表
const loadUserSignIns = async () => {
  if (!user.id) return
  try {
    const res = await activityApi.mySignins({ userId: user.id })
    mySignIns.value = res || []
  } catch {
    mySignIns.value = []
  }
}

// 加载活动列表
const loadActivities = async () => {
  loading.value = true
  try {
    if (filterType.value === 'upcoming') {
      const res = await activityApi.list({ clubId: clubId.value })
      const now = new Date()
      activities.value = (res || []).filter(a => new Date(a.time) > now)
    } else {
      const res = await activityApi.mySigned({ userId: user.id })
      activities.value = res || []
    }
    await Promise.all([loadUserSigns(), loadUserSignIns()])
  } catch (e) {
    console.error(e)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 判断是否已报名
const isSigned = (activity) => mySigns.value.includes(activity.id)

// 判断是否已签到
const isSignedIn = (activity) => mySignIns.value.includes(activity.id)

// 报名
const handleSign = async (activity) => {
  const res = await activityApi.sign({ activityId: activity.id })
  ElMessage.success(res)
  await loadActivities()
}

// 取消报名
const handleCancel = async (activity) => {
  const res = await activityApi.cancel({ activityId: activity.id })
  ElMessage.success(res)
  await loadActivities()
}

// 查看报名名单
const showSignList = async (activity) => {
  signList.value = await activityApi.signList(activity.id)
  listDialog.value = true
}

// 管理员生成签到码
const generateCode = async (activity) => {
  try {
    const code = await activityApi.generateCode(activity.id)
    signCode.value = code
    currentActivityId.value = activity.id
    codeDialog.value = true
  } catch {
    ElMessage.error('生成签到码失败')
  }
}

// 复制签到码
const copyCode = () => {
  navigator.clipboard.writeText(signCode.value)
  ElMessage.success('签到码已复制')
}

// 打开成员签到弹窗
const openSignDialog = (activity) => {
  if (isSignedIn(activity)) {
    ElMessage.warning('您已签到过')
    return
  }
  currentSignActivity.value = activity
  signCodeInput.value = ''
  signDialogVisible.value = true
}

// 提交签到
const submitSign = async () => {
  if (!signCodeInput.value) {
    ElMessage.warning('请输入签到码')
    return
  }
  try {
    const res = await activityApi.signin({
      activityId: currentSignActivity.value.id,
      code: signCodeInput.value
    })
    ElMessage.success(res)
    signDialogVisible.value = false
    await loadActivities()
  } catch {
    ElMessage.error('签到失败，请检查签到码')
  }
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.activity-list {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}
.page-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}
.sign-code {
  text-align: center;
}
.code {
  font-size: 32px;
  font-weight: bold;
  letter-spacing: 4px;
  background: #f0f2f6;
  display: inline-block;
  padding: 12px 24px;
  border-radius: 12px;
  margin: 12px 0;
}
/* 自定义禁用按钮样式，确保视觉上不可点击 */
.disabled-sign-btn,
.el-button.disabled-sign-btn,
.el-button.disabled-sign-btn.is-disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background-color: #a0cfff !important;
  border-color: #a0cfff !important;
  color: #fff !important;
}
</style>