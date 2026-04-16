import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = token
  }
  return config
})

request.interceptors.response.use(
  response => response.data,
  error => {
    ElMessage.error(error.response?.data?.msg || '网络错误')
    return Promise.reject(error)
  }
)

// 活动相关
export const activityApi = {
  list: (params) => request.get('/activity/list', { params }),
  sign: (data) => request.post('/activity/sign', data),
  add: (data) => request.post('/activity/add', data),
  cancel: (data) => request.post('/activity/cancel', data),
  signList: (activityId) => request.get('/activity/sign-list', { params: { activityId } }),
  generateCode: (activityId) => request.post('/activity/generate-code', null, { params: { activityId } }),
  signin: (data) => request.post('/activity/signin', data),
  mySignins: (params) => request.get('/activity/my-signins', { params }),
  mySigned: (params) => request.get('/activity/my-signed', { params })   // 曾参与活动
}

// 社团相关
export const clubApi = {
   list: (params) => request.get('/club/list', { params }),
  apply: (data) => request.post('/club/apply', data),
  uploadLogo: (clubId, file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post(`/club/upload-logo/${clubId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

// 用户相关
export const userApi = {
  login: (data) => request.post('/user/login', data),
  register: (data) => request.post('/user/register', data),
  update: (data) => request.put('/user/update', data),
  uploadAvatar: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/user/upload-avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

// 入社申请
export const memberApplyApi = {
  add: (data) => request.post('/member-apply/add', data),
  audit: (applyId, status) => request.put('/member-apply/audit', null, { params: { applyId, status } }),
  pendingList: (clubId) => request.get('/member-apply/pending-list', { params: { clubId } })
}

// 社团公告
export const clubNoticeApi = {
  list: (clubId) => request.get('/club/notice/list', { params: { clubId } }),
  add: (data) => request.post('/club/notice/add', data),
  setTop: (id, top) => request.put(`/club/notice/top/${id}`, null, { params: { top } })
}

// 社团成员
export const clubMemberApi = {
  list: (clubId) => request.get('/club/member/list', { params: { clubId } }),
  setRole: (clubId, userId, role) => request.put('/club/member/role', null, { params: { clubId, userId, role } }),
  remove: (clubId, userId) => request.delete('/club/member/remove', { params: { clubId, userId } })
}

// 系统通知
export const noticeApi = {
  list: (userId) => request.get('/notice/list', { params: { userId } }),
  add: (data) => request.post('/notice/add', data),
  markRead: (noticeId, userId) => request.post(`/notice/mark-read/${noticeId}`, null, { params: { userId } }),
  unreadCount: (userId) => request.get('/notice/unread-count', { params: { userId } })
}

export default request