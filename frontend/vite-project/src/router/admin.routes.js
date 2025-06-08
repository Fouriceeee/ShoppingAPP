/**
 * 管理员相关路由配置
 */

// 管理员认证相关页面
const AdminLogin = () => import('@/views/Admin/AdminLogin.vue')
const AdminRegister = () => import('@/views/Admin/AdminRegister.vue')

// 管理控制台主页
const AdminDashboard = () => import('@/views/Admin/Dashboard.vue')

// 商品管理相关页面
const ProductsList = () => import('@/views/Admin/ProductManagement/ProductsList.vue')
const AddProduct = () => import('@/views/Admin/ProductManagement/AddProduct.vue')
const EditProduct = () => import('@/views/Admin/ProductManagement/EditProduct.vue')

/*
// 用户管理相关页面
const UsersList = () => import('@/views/Admin/UserManagement/UsersList.vue')

// 订单管理相关页面
const OrdersList = () => import('@/views/Admin/OrderManagement/OrdersList.vue')
const OrderDetail = () => import('@/views/Admin/OrderManagement/OrderDetail.vue')
*/

/**
 * 管理员路由配置
 * 使用懒加载提高性能
 */
const adminRoutes = [
  // 认证相关路由 - 不需要登录
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: AdminLogin,
    meta: {
      requiresAuth: false,
      title: '管理员登录 - 易猫商城'
    }
  },
  {
    path: '/admin/register',
    name: 'AdminRegister',
    component: AdminRegister,
    meta: {
      requiresAuth: false,
      title: '管理员注册 - 易猫商城'
    }
  },

  // 管理控制台 - 需要管理员权限
  {
    path: '/admin',
    name: 'AdminRoot',
    redirect: '/admin/dashboard',
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: {
      requiresAuth: true,
      requiresAdmin: true,
      title: '管理控制台 - 易猫商城'
    }
  },

  // 商品管理路由
  {
    path: '/admin/products',
    name: 'ProductsList',
    component: ProductsList,
    meta: {
      requiresAuth: true,
      requiresAdmin: true,
      title: '商品管理 - 易猫商城'
    }
  },
  {
    path: '/admin/products/add',
    name: 'AddProduct',
    component: AddProduct,
    meta: {
      requiresAuth: true,
      requiresAdmin: true,
      title: '添加商品 - 易猫商城'
    }
  },
  {
    path: '/admin/products/edit/:id',
    name: 'EditProduct',
    component: EditProduct,
    props: true,
    meta: {
      requiresAuth: true,
      requiresAdmin: true,
      title: '编辑商品 - 易猫商城'
    }
  },

/*  // 用户管理路由
  {
    path: '/admin/users',
    name: 'UsersList',
    component: UsersList,
    meta: {
      requiresAuth: true,
      requiresAdmin: true,
      title: '用户管理 - 易猫商城'
    }
  },*/

/*  // 订单管理路由
  {
    path: '/admin/orders',
    name: 'OrdersList',
    component: OrdersList,
    meta: {
      requiresAuth: true,
      requiresAdmin: true,
      title: '订单管理 - 易猫商城'
    }
  },*/
/*  {
    path: '/admin/orders/:id',
    name: 'OrderDetail',
    component: OrderDetail,
    props: true,
    meta: {
      requiresAuth: true,
      requiresAdmin: true,
      title: '订单详情 - 易猫商城'
    }
  }*/
]

export default adminRoutes
