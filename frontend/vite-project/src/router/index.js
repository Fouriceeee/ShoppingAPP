import {createRouter, createWebHistory} from "vue-router";
import HomeView from "../views/Home/HomeView.vue";
import Products from "../views/Products/products.vue";
import ProductDetail from "@/views/Products/ProductDetail.vue";
import CartView from "../views/Cart/CartView.vue";
import LoginView from "@/views/Login/LoginView.vue";
import RegisterView from "@/views/Login/RegisterView.vue";
import UserView from "@/views/User/UserView.vue";
import SearchView from "@/views/Search/SearchView.vue";
import { checkAuth } from '@/utils/userService';
import { ElMessage } from 'element-plus';
import {checkAdminAuth} from "@/utils/adminService.js";
import adminRoutes from "@/router/admin.routes.js";
import CheckoutView from "@/views/Checkout/CheckoutView.vue";
import AboutView from "@/views/About/AboutView.vue";


/**
 * 前台客户路由配置
 */
const customerRoutes = [
    {
        path: '/',
        name: 'Home',
        component: HomeView,
        meta: {
            title: '首页 - 易猫商城'
        }
    },
    {
        path: '/search',
        name: 'Search',
        component: SearchView,
        meta: {
            title: '搜索商品 - 易猫商城'
        }
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginView,
        meta: {
            title: '登录 - 易猫商城'
        }
    },
    {
        path: '/register',
        name: 'Register',
        component: RegisterView,
        meta: {
            title: '注册 - 易猫商城'
        }
    },
    {
        path: '/products',
        name: 'Products',
        component: Products,
        props: true,
        meta: {
            title: '所有商品 - 易猫商城'
        }
    },
    {
        path: '/products/:id',
        name: 'ProductDetail',
        component: ProductDetail,
        props: true,
        meta: {
            title: '商品详情 - 易猫商城'
        }
    },
    {
        path: '/user',
        name: 'User',
        component: UserView,
        meta: {
            requiresAuth: true,
            title: '个人中心 - 易猫商城'
        }
    },
    {
        path: '/cart',
        name: 'Cart',
        component: CartView,
        meta: {
            requiresAuth: true,
            title: '购物车 - 易猫商城'
        }
    },
    {
        path: '/checkout',
        name: 'Checkout',
        component: CheckoutView,
        meta: { requiresAuth: true }
    },
    {
        path: '/about',
        name: 'About',
        component: AboutView,
        meta: {
            title: '关于我们'
        }
    }
/*    {
        path: '/checkout',
        name: 'Checkout',
        component: CheckoutView,
        meta: {
            requiresAuth: true,
            title: '结算 - 易猫商城'
        }
    },*/
/*    {
        path: '/:pathMatch(.*)*',
        name: 'Not-found',
        component: NotFoundView,
        meta: {
            title: '页面未找到 - 易猫商城'
        }
    }*/
]

/**
 * 合并所有路由
 */
const routes = [
    ...customerRoutes,
    ...adminRoutes
]


const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
    scrollBehavior() {
        // 始终滚动到顶部
        return { top: 0 }
    },
})


// 全局前置守卫，验证用户权限和管理员权限
router.beforeEach((to, from, next) => {
    // 检查是否需要管理员权限
    if (to.matched.some(record => record.meta.requiresAdmin)) {
        // 验证管理员是否已登录
        if (!checkAdminAuth()) {
            ElMessage.error('请先登录管理员账号')
            next({ path: '/admin/login' })
            return
        }
        // 管理员已登录，放行
        next()
        return
    }

    // 检查是否需要普通用户权限
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
