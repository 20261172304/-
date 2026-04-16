<template>
  <div class="admin-dashboard">
    <h2>系统管理</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="创建社团" name="createClub">
        <el-card class="create-club-card">
          <template #header>
            <span>创建新社团</span>
          </template>
          <el-form :model="clubForm" label-width="80px" @submit.prevent="createClub">
            <el-form-item label="社团名称" required>
              <el-input v-model="clubForm.name" placeholder="请输入社团名称" />
            </el-form-item>
            <el-form-item label="社团简介">
              <el-input type="textarea" v-model="clubForm.description" rows="3" placeholder="请输入社团简介" />
            </el-form-item>
            <el-form-item label="社长姓名">
              <el-input v-model="clubForm.leader" placeholder="社长姓名（可选）" />
            </el-form-item>
            <el-form-item label="社长ID">
              <el-input-number v-model="clubForm.leaderId" :min="1" placeholder="社长用户ID（可选）" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" native-type="submit" :loading="submitting">立即创建</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="发布通知" name="publishNotice">
        <el-card class="publish-notice-card">
          <template #header>
            <span>发布系统通知</span>
          </template>
          <el-form :model="noticeForm" label-width="80px" @submit.prevent="publishNotice">
            <el-form-item label="标题" required>
              <el-input v-model="noticeForm.title" placeholder="请输入通知标题" />
            </el-form-item>
            <el-form-item label="内容" required>
              <el-input type="textarea" v-model="noticeForm.content" rows="5" placeholder="请输入通知内容" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" native-type="submit" :loading="noticeLoading">发布通知</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api'

const activeTab = ref('createClub')

// 创建社团
const clubForm = reactive({
  name: '',
  description: '',
  leader: '',
  leaderId: null
})
const submitting = ref(false)

const createClub = async () => {
  if (!clubForm.name) {
    ElMessage.warning('请填写社团名称')
    return
  }
  submitting.value = true
  try {
    const res = await request.post('/club/admin/create', clubForm)
    if (res.code === 200) {
      ElMessage.success('社团创建成功')
      clubForm.name = ''
      clubForm.description = ''
      clubForm.leader = ''
      clubForm.leaderId = null
    } else {
      ElMessage.error(res.msg || '创建失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  } finally {
    submitting.value = false
  }
}

// 发布系统通知
const noticeForm = reactive({
  title: '',
  content: ''
})
const noticeLoading = ref(false)

const publishNotice = async () => {
  if (!noticeForm.title || !noticeForm.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  noticeLoading.value = true
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const res = await request.post('/notice/add', {
      title: noticeForm.title,
      content: noticeForm.content,
      adminId: user.id
    })
    if (res === '发布成功') {
      ElMessage.success('通知发布成功')
      noticeForm.title = ''
      noticeForm.content = ''
    } else {
      ElMessage.error(res || '发布失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  } finally {
    noticeLoading.value = false
  }
}
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}
.create-club-card,
.publish-notice-card {
  max-width: 600px;
  margin-top: 20px;
}
</style>