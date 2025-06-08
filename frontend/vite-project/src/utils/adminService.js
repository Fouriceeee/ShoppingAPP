/**
 * 管理员服务模块 - 管理员账号管理和权限验证
 */

// 从localStorage获取管理员列表
const getAdminsFromStorage = () => {
  const adminsJson = localStorage.getItem('admins')
  return adminsJson ? JSON.parse(adminsJson) : []
}

// 将管理员列表保存到localStorage
const saveAdminsToStorage = (admins) => {
  localStorage.setItem('admins', JSON.stringify(admins))
}

// 初始化默认管理员账号（如果不存在）
const initDefaultAdmin = () => {
  const admins = getAdminsFromStorage()

  // 如果没有管理员，创建一个默认管理员
  if (admins.length === 0) {
    const defaultAdmin = {
      id: 'admin1',
      username: 'admin',
      password: 'admin123',  // 实际应用中应该使用加密密码
      name: '系统管理员',
      role: 'superadmin',
      permissions: ['all'],
      avatar: '/src/assets/pictures/loginImages/default-avatar.png',
      createdAt: new Date().toISOString()
    }

    admins.push(defaultAdmin)
    saveAdminsToStorage(admins)
    console.log('已创建默认管理员账号')
  }
}

// 设置当前登录的管理员
const setCurrentAdmin = (admin) => {
  // 存储不包含密码的管理员信息
  const { password, ...safeAdminInfo } = admin
  localStorage.setItem('currentAdmin', JSON.stringify(safeAdminInfo))
}

// 获取当前登录的管理员
const getCurrentAdmin = () => {
  const adminJson = localStorage.getItem('currentAdmin')
  return adminJson ? JSON.parse(adminJson) : null
}

// 管理员退出登录
const logout = () => {
  localStorage.removeItem('currentAdmin')
}

/**
 * 管理员登录
 * @param {string} username - 管理员用户名
 * @param {string} password - 密码
 * @returns {Promise} 包含管理员数据的Promise
 */
export const adminLogin = (username, password) => {
  return new Promise((resolve, reject) => {
    // 确保默认管理员存在
    initDefaultAdmin()

    // 模拟网络延迟
    setTimeout(() => {
      const admins = getAdminsFromStorage()

      // 查找管理员
      const admin = admins.find(a =>
        a.username === username && a.password === password
      )

      if (admin) {
        // 登录成功，设置当前管理员
        setCurrentAdmin(admin)
        resolve({ admin, message: '管理员登录成功' })
      } else {
        // 登录失败
        reject({ message: '管理员账号或密码不正确' })
      }
    }, 1000) // 1秒延迟模拟网络请求
  })
}

/**
 * 检查管理员是否已登录
 * @returns {Object|null} 当前登录的管理员，如果未登录则返回null
 */
export const checkAdminAuth = () => {
  return getCurrentAdmin()
}

/**
 * 管理员退出登录
 */
export const adminLogout = () => {
  logout()
  return { message: '已成功退出管理系统' }
}

/**
 * 检查管理员权限
 * @param {string} permission - 权限标识
 * @returns {boolean} 是否有权限
 */
export const checkPermission = (permission) => {
  const admin = getCurrentAdmin()
  if (!admin) return false

  // 超级管理员拥有所有权限
  if (admin.role === 'superadmin' || admin.permissions.includes('all')) {
    return true
  }

  // 检查特定权限
  return admin.permissions.includes(permission)
}

/**
 * 获取管理员列表
 * @returns {Promise} 包含管理员列表的Promise
 */
export const getAdminList = () => {
  return new Promise((resolve) => {
    const admins = getAdminsFromStorage()
    // 不返回密码字段
    const safeAdmins = admins.map(({ password, ...admin }) => admin)
    resolve(safeAdmins)
  })
}

/**
 * 添加新管理员
 * @param {Object} adminData - 管理员数据
 * @returns {Promise} 操作结果Promise
 */
export const addAdmin = (adminData) => {
  return new Promise((resolve, reject) => {
    const admins = getAdminsFromStorage()

    // 检查用户名是否已存在
    if (admins.some(admin => admin.username === adminData.username)) {
      return reject({ message: '管理员用户名已存在' })
    }

    // 创建新管理员
    const newAdmin = {
      ...adminData,
      id: `admin${Date.now()}`,
      createdAt: new Date().toISOString()
    }

    admins.push(newAdmin)
    saveAdminsToStorage(admins)

    // 不返回密码字段
    const { password, ...safeAdmin } = newAdmin
    resolve({ admin: safeAdmin, message: '管理员添加成功' })
  })
}
