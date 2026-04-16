<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">
          <el-icon :size="48"><School /></el-icon>
        </div>
        <h2>社团管理系统</h2>
        <p>欢迎回来，请登录您的账号</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" native-type="submit" :loading="loading" class="login-btn">登录</el-button>
        </el-form-item>
        <div class="login-footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, School } from '@element-plus/icons-vue'
import request from '../api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await request.post('/user/login', form)
    if (res.code === 200) {
      localStorage.setItem('token', res.token)
      localStorage.setItem('user', JSON.stringify(res.user))
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.msg || '登录失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 420px;
  padding: 40px 32px;
  background: rgba(255,255,255,0.95);
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
  backdrop-filter: blur(10px);
  transition: all 0.3s;
}
.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.logo-icon {
  color: #409eff;
  margin-bottom: 16px;
}
.login-header h2 {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 600;
  color: #1f2d3d;
}
.login-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}
.login-btn {
  width: 100%;
  border-radius: 40px;
  font-weight: 600;
}
.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #606266;
}
.login-footer span {
  margin-right: 8px;
}
</style>