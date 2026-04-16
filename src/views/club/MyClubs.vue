<template>
  <div>
    <h2>我的社团</h2>
    <el-table :data="managedClubs">
      <el-table-column prop="name" label="社团名称" />
      <el-table-column prop="description" label="简介" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="goToManage(row.id)">管理社团</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { clubApi } from '../../api'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const managedClubs = ref([])

const loadManagedClubs = async () => {
  const clubIds = user.managedClubIds || []
  if (clubIds.length === 0) return
  const allClubs = await clubApi.list({})
  managedClubs.value = allClubs.filter(c => clubIds.includes(c.id))
}

const goToManage = (clubId) => {
  router.push({ name: 'ClubDetail', params: { id: clubId } })
}

onMounted(loadManagedClubs)
</script>