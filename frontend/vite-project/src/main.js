import { createApp } from 'vue'
import './assets/css/common.css'
import App from './App.vue'
import router from "./router/index.js";
import * as ElementPlusIconsVue from '@element-plus/icons-vue'


const app = createApp(App)

app.use(router)

app.mount('#app')

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
