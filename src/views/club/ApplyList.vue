<template>
  <div class="apply-list">
    <div class="page-header">
      <h2>入社申请管理</h2>
      <el-button type="primary" :icon="Refresh" @click="loadApplications">刷新</el-button>
    </div>
    <el-table :data="applications" stripe v-loading="loading" border>
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="clubId" label="社团ID" width="100" />
      <el-table-column prop="reason" label="申请理由" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PENDING' ? 'warning' : (row.status === 'APPROVED' ? 'success' : 'danger')">
            {{ row.status === 'PENDING' ? '待审核' : (row.status === 'APPROVED' ? '已通过' : '已拒绝') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" width="180">
        <template #default="{ row }">
          {{ new Date(row.createTime).toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'PENDING'" size="small" type="success" plain @click="handleStatus(row, 'APPROVED')">通过</el-button>
          <el-button v-if="row.status === 'PENDING'" size="small" type="danger" plain @click="handleStatus(row, 'REJECTED')">拒绝</el-button>
          <span v-else>-</span>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!loading && applications.length === 0" description="暂无待处理申请" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { memberApplyApi } from '../../api'

const applications = ref([])
const loading = ref(false)
const user = JSON.parse(localStorage.getItem('user') || '{}')

// 获取当前用户管理的第一个社团ID（实际业务中可能有多个，此处取第一个）
const getManagedClubId = () => {
  const managedClubs = user.managedClubIds || []
  return managedClubs.length > 0 ? managedClubs[0] : null
}

const loadApplications = async () => {
  const clubId = getManagedClubId()
  if (!clubId) {
    applications.value = []
    return
  }
  loading.value = true
  try {
    const list = await memberApplyApi.pendingList(clubId)
    applications.value = list
  } catch (e) {
    ElMessage.error('加载申请列表失败')
  } finally {
    loading.value = false
  }
}

const handleStatus = async (row, status) => {
  const actionText = status === 'APPROVED' ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确认${actionText}该入社申请吗？`, '操作确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await memberApplyApi.audit(row.id, status)
    if (res === '操作成功') {
      ElMessage.success('操作成功')
      loadApplications()
    } else {
      ElMessage.error(res || '操作失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(loadApplications)
</script>

<style scoped>
.apply-list {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}
</style>