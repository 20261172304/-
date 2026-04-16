<template>
  <div>
    <h2>社团申请审批</h2>
    <el-table :data="pendingClubs">
      <el-table-column prop="name" label="社团名称" />
      <el-table-column prop="description" label="简介" />
      <el-table-column prop="leader" label="社长姓名" />
      <el-table-column prop="createTime" label="申请时间" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="handleAudit(row, 'APPROVED')">通过</el-button>
          <el-button size="small" type="danger" @click="handleAudit(row, 'REJECTED')">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../api'

const pendingClubs = ref([])

const loadPending = async () => {
  const res = await request.get('/club/pending-list')
  if (res.code === 200) {
    pendingClubs.value = res.data
  }
}

const handleAudit = async (club, status) => {
  const res = await request.put(`/club/audit/${club.id}?status=${status}`)
  if (res.code === 200) {
    ElMessage.success('操作成功')
    loadPending()
  } else {
    ElMessage.error(res.msg)
  }
}

onMounted(loadPending)
</script>