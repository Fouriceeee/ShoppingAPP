import {createRouter, createWebHistory} from "vue-router";
import HomeView from "../views/Home/HomeView.vue";
import Products from "../views/Products/products.vue";
import CartView from "../views/Cart/CartView.vue";
import LoginView from "@/views/Login/LoginView.vue";


const routes = [
    {/*主页面*/
        path: "/",
        name: "Home",
        component: HomeView
    },
    {/*商品页面*/
        path: "/products",
        name: "Products",
        component: Products
    },
    {/*购物车*/
        path: "/cart",
        name: "CartPage",
        component: CartView
    },
    {/*登录页面*/
        path: "/login",
        name: "LoginPage",
        component: LoginView
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

export default router