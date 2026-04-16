<template>
  <div class="notice-center">
    <div class="page-header">
      <h2>系统通知</h2>
      <el-button :icon="Refresh" @click="loadNotices">刷新</el-button>
    </div>
    <el-table :data="notices" stripe v-loading="loading">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column prop="createTime" label="时间" width="180" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.read" type="success">已读</el-tag>
          <el-tag v-else type="info">未读</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button v-if="!scope.row.read" size="small" type="primary" plain @click="markRead(scope.row)">标记已读</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { noticeApi } from '../api'

const notices = ref([])
const loading = ref(false)
const user = JSON.parse(localStorage.getItem('user') || '{}')

const loadNotices = async () => {
  if (!user.id) return
  loading.value = true
  try {
    const list = await noticeApi.list(user.id)
    notices.value = list
  } catch (e) {
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

const markRead = async (notice) => {
  try {
    await noticeApi.markRead(notice.id, user.id)
    notice.read = true
    ElMessage.success('已标记为已读')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadNotices()
})
</script>

<style scoped>
.notice-center {
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