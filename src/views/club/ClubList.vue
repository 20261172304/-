<template>
  <div class="club-list">
    <div class="page-header">
      <h2>社团广场</h2>
      <el-input v-model="searchKeyword" placeholder="搜索社团名称" clearable style="width: 260px" @clear="fetchClubs" @keyup.enter="fetchClubs">
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>
    <el-row :gutter="20" v-loading="loading">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="club in clubs" :key="club.id" style="margin-bottom: 20px">
        <el-card class="club-card" shadow="hover" @click="viewDetail(club)">
          <div class="club-avatar">
            <el-avatar :size="60" :src="club.logo || defaultClubLogo" />
          </div>
          <div class="club-info">
            <h3>{{ club.name }}</h3>
            <p class="leader">社长：{{ club.leader || '暂无' }}</p>
            <p class="desc">{{ club.description || '暂无简介' }}</p>
          </div>
          <template #footer>
            <el-button size="small" type="primary" plain @click.stop="viewActivities(club)">查看活动</el-button>
            <el-button size="small" type="success" plain @click.stop="viewDetail(club)">社团详情</el-button>
          </template>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { clubApi } from '../../api'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'

const clubs = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const router = useRouter()
const defaultClubLogo = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const fetchClubs = async () => {
  loading.value = true
  try {
    const data = await clubApi.list({ name: searchKeyword.value || undefined })
    clubs.value = data
  } finally {
    loading.value = false
  }
}
const viewActivities = (club) => {
  router.push({ path: '/activities', query: { clubId: club.id } })
}
const viewDetail = (club) => {
  router.push({ name: 'ClubDetail', params: { id: club.id } })
}
watch(searchKeyword, () => fetchClubs())
onMounted(fetchClubs)
</script>

<style scoped>
.club-list {
  max-width: 1400px;
  margin: 0 auto;
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
  font-size: 24px;
  font-weight: 600;
  color: #1f2d3d;
}
.club-card {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 12px;
  overflow: hidden;
}
.club-card:hover {
  transform: translateY(-4px);
}
.club-avatar {
  text-align: center;
  padding: 20px 0 10px;
  color: #409eff;
}
.club-info h3 {
  margin: 8px 0;
  font-size: 18px;
  text-align: center;
}
.leader {
  color: #909399;
  font-size: 14px;
  text-align: center;
  margin: 4px 0;
}
.desc {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
  height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}
:deep(.el-card__footer) {
  display: flex;
  justify-content: center;
  gap: 12px;
  background: #fafafa;
}
</style>