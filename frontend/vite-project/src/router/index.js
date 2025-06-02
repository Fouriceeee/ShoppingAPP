import {createRouter, createWebHistory} from "vue-router";
import HomeView from "../views/Home/HomeView.vue";
import Products from "../views/Products/products.vue";
import CartView from "../views/Cart/CartView.vue";


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
    {
        path: "/cart",
        name: "CartPage",
        component: CartView
    }


]
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
})

export default router