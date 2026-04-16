<template>
  <div class="profile-page">
    <el-card class="profile-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>个人信息</h2>
          <el-tag :type="userInfo.role === 'ADMIN' ? 'danger' : 'success'">
            {{ userInfo.role === 'ADMIN' ? '超级管理员' : '普通用户' }}
          </el-tag>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="头像">
          <div class="avatar-section">
            <el-avatar :size="80" :src="displayAvatar" />
            <el-button type="primary" plain @click="uploadAvatar">更换头像</el-button>
            <input type="file" ref="fileInput" style="display:none" accept="image/jpeg,image/png" @change="handleFileChange" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateProfile" :loading="updating">保存修改</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, computed, getCurrentInstance, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '../../api'

const instance = getCurrentInstance()
const bus = instance.proxy.$bus

// 用户信息（响应式，用于模板显示）
const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{}'))

// 获取最新用户信息的函数
const getLatestUserInfo = () => JSON.parse(localStorage.getItem('user') || '{}')

const formRef = ref()
const updating = ref(false)
const fileInput = ref(null)

// 表单数据
const form = reactive({
  username: '',
  email: '',
  avatar: ''
})

// 存储初始快照（页面加载时的邮箱和头像）
const initialForm = reactive({
  email: '',
  avatar: ''
})

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 显示头像的完整URL
const displayAvatar = computed(() => {
  if (!form.avatar) return defaultAvatar
  if (form.avatar.startsWith('http')) return form.avatar
  if (form.avatar.startsWith('/uploads/')) return `http://localhost:8080${form.avatar}`
  return defaultAvatar
})

// 校验规则
const rules = {
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
}

// 初始化表单
const initForm = () => {
  const latest = getLatestUserInfo()
  userInfo.value = latest
  form.username = latest.username || ''
  form.email = latest.email || ''
  form.avatar = latest.avatar || ''
  // 保存初始快照（进入页面时的状态）
  initialForm.email = form.email
  initialForm.avatar = form.avatar
  console.log('初始化表单数据:', { ...form })
}

// 重置表单（恢复到初始快照，而不是 localStorage 的最新值）
const resetForm = () => {
  console.log('重置按钮被点击')
  form.email = initialForm.email
  form.avatar = initialForm.avatar
  console.log('重置后的表单数据:', { email: form.email, avatar: form.avatar })
  ElMessage.success('已重置为进入页面时的状态')
}

// 上传头像
const uploadAvatar = () => fileInput.value.click()

const handleFileChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await userApi.uploadAvatar(file)
    if (res.code === 200) {
      form.avatar = res.url
      ElMessage.success('头像上传成功')
      // 更新本地存储
      const latest = getLatestUserInfo()
      latest.avatar = res.url
      localStorage.setItem('user', JSON.stringify(latest))
      userInfo.value = latest
      // 注意：不更新 initialForm.avatar，这样重置时仍会回到页面加载时的头像
      // 触发全局事件更新左侧头像
      bus.emit('avatar-updated')
    } else {
      ElMessage.error(res.msg || '上传失败')
    }
  } catch {
    ElMessage.error('上传失败，请检查网络')
  }
}

// 保存修改
const updateProfile = async () => {
  if (formRef.value) await formRef.value.validate()
  updating.value = true
  try {
    const res = await userApi.update({ email: form.email, avatar: form.avatar })
    if (res.code === 200) {
      ElMessage.success('个人信息更新成功')
      // 更新本地存储
      const latest = getLatestUserInfo()
      latest.email = form.email
      latest.avatar = form.avatar
      localStorage.setItem('user', JSON.stringify(latest))
      userInfo.value = latest
      // 注意：不更新 initialForm，保持页面加载时的快照
      // 触发全局事件更新左侧头像
      bus.emit('avatar-updated')
    } else {
      ElMessage.error(res.msg || '更新失败')
    }
  } catch {
    ElMessage.error('网络错误')
  } finally {
    updating.value = false
  }
}

// 页面加载时初始化表单
onMounted(initForm)
</script>

<style scoped>
.profile-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 120px);
  padding: 24px;
}
.profile-card {
  max-width: 700px;
  width: 100%;
  border-radius: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}
</style>