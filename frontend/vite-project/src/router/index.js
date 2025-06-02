import {createRouter, createWebHistory} from "vue-router";
import Home from "../views/Home/home.vue";
import Products from "../views/Products/products.vue";
import Cart from "../views/Cart/cart.vue";


const routes = [
    {/*主页面*/
        path: "/",
        name: "Home",
        component: Home
    },
    {/*商品页面*/
        path: "/products",
        name: "Products",
        component: Products
    },
    {
        path: "/cart",
        name: "CartPage",
        component: Cart
    }


]
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
})

export default router