<template>
  <div>
    <h2>申请创建社团</h2>
    <el-form :model="form" label-width="80px" @submit.prevent="submitApply">
      <el-form-item label="社团名称" required>
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="社团简介">
        <el-input type="textarea" v-model="form.description" rows="3" />
      </el-form-item>
      <el-form-item label="社长姓名">
        <el-input v-model="form.leader" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit" :loading="submitting">提交申请</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { clubApi } from '../../api'

const form = reactive({
  name: '',
  description: '',
  leader: ''
})
const submitting = ref(false)

const submitApply = async () => {
  if (!form.name) {
    ElMessage.warning('请填写社团名称')
    return
  }
  submitting.value = true
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const res = await clubApi.apply({ ...form, leaderId: user.id, status: 'PENDING' })
    if (res === '申请已提交') {
      ElMessage.success('申请已提交，等待管理员审批')
      form.name = ''
      form.description = ''
      form.leader = ''
    } else {
      ElMessage.error(res || '提交失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  } finally {
    submitting.value = false
  }
}
</script>