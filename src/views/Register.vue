<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <el-icon :size="48"><School /></el-icon>
        <h2>用户注册</h2>
        <p>加入社团大家庭</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱" size="large" :prefix-icon="Message" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" native-type="submit" :loading="loading" class="register-btn">注册</el-button>
        </el-form-item>
        <div class="register-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="$router.push('/login')">去登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, School } from '@element-plus/icons-vue'
import { userApi } from '../api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  email: ''
})
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await userApi.register(form)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.msg || '注册失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.register-card {
  width: 420px;
  padding: 40px 32px;
  background: rgba(255,255,255,0.95);
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
}
.register-header {
  text-align: center;
  margin-bottom: 32px;
  color: #409eff;
}
.register-header h2 {
  margin: 12px 0 8px;
  font-size: 28px;
  font-weight: 600;
  color: #1f2d3d;
}
.register-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}
.register-btn {
  width: 100%;
  border-radius: 40px;
  font-weight: 600;
}
.register-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #606266;
}
</style>