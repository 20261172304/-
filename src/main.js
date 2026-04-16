import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)

// 简易事件总线
const eventBus = {
  events: {},
  emit(event, data) {
    if (this.events[event]) {
      this.events[event].forEach(callback => callback(data))
    }
  },
  on(event, callback) {
    if (!this.events[event]) this.events[event] = []
    this.events[event].push(callback)
  },
  off(event, callback) {
    if (this.events[event]) {
      const index = this.events[event].indexOf(callback)
      if (index !== -1) this.events[event].splice(index, 1)
    }
  }
}
app.config.globalProperties.$bus = eventBus

app.use(router)
app.use(ElementPlus)
app.mount('#app')