import {createRouter, createWebHistory} from "vue-router";
import HomeView from "../views/Home/HomeView.vue";
import Products from "../views/Products/products.vue";
import CartView from "../views/Cart/CartView.vue";
import LoginView from "@/views/Login/LoginView.vue";
import RegisterView from "@/views/Login/RegisterView.vue";
import { checkAuth } from '@/utils/userService';
import { ElMessage } from 'element-plus';


const routes = [
    {/*主页面*/
        path: "/",
        name: "Home",
        component: HomeView,
    },
    {/*商品页面*/
        path: "/products",
        name: "Products",
        component: Products
    },
    {/*商品详情页*/
        path: "/products/:id",
        name: "ProductDetail",
        component: () => import('../views/Products/ProductDetail.vue')
    },
    {/*购物车*/
        path: "/cart",
        name: "CartPage",
        component: CartView,
        meta: { requiresAuth: true }
    },
    {/*用户主页*/
        path: "/user",
        name: "UserPage",
        component: () => import('../views/User/UserPage.vue'),
        meta: { requiresAuth: true, title: '个人中心 - 易猫商城' }
    },
    {/*登录页面*/
        path: "/login",
        name: "LoginPage",
        component: LoginView
    },
    {/*注册页面*/
        path: "/register",
        name: "RegisterPage",
        component: RegisterView
    },
    /*{/!*关于*!/
        path: "/about",
        name: "AboutPage",
        component: AboutPage
    },*/
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
})

// 全局前置守卫，验证用户是否登录
router.beforeEach((to, from, next) => {
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
    const currentUser = checkAuth()

    if (requiresAuth && !currentUser) {
        // 如果需要登录但用户未登录，重定向到登录页面
        ElMessage.warning('请先登录再继续操作')
        next({
            path: '/login',
            query: { redirect: to.fullPath } // 保存原来要访问的路径，登录后可以重定向回去
        })
    } else {
        // 不需要验证或已登录，正常导航
        next()
    }
})

export default router