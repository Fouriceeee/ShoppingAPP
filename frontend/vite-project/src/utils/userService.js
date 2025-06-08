/**
 * 用户服务模块 - 使用localStorage存储用户数据
 */

// 从localStorage获取用户列表
const getUsersFromStorage = () => {
  const usersJson = localStorage.getItem('users')
  return usersJson ? JSON.parse(usersJson) : []
}

// 将用户列表保存到localStorage
const saveUsersToStorage = (users) => {
  localStorage.setItem('users', JSON.stringify(users))
}

// 设置当前登录用户
const setCurrentUser = (user) => {
  // 存储不包含密码的用户信息
  const { password, confirmPassword, ...safeUserInfo } = user
  localStorage.setItem('currentUser', JSON.stringify(safeUserInfo))
}

// 获取当前登录用户
const getCurrentUser = () => {
  const userJson = localStorage.getItem('currentUser')
  return userJson ? JSON.parse(userJson) : null
}

// 退出登录
const logout = () => {
  localStorage.removeItem('currentUser')
}

/**
 * 用户注册
 * @param {Object} userData - 用户注册数据
 * @returns {Promise} 包含成功/失败信息的Promise
 */
export const register = (userData) => {
  return new Promise((resolve, reject) => {
    // 模拟网络延迟
    setTimeout(async () => {
      const users = getUsersFromStorage()

      // 检查用户名是否已存在
      const existingUser = users.find(user => user.username === userData.username)
      if (existingUser) {
        return reject({message: '用户名已被注册'})
      }

      // 检查邮箱是否已存在
      const existingEmail = users.find(user => user.email === userData.email)
      if (existingEmail) {
        return reject({message: '该邮箱已被注册'})
      }

      // 创建新用户 (不存储确认密码字段)
      const {confirmPassword, ...newUser} = userData
      newUser.id = Date.now().toString() // 简单的ID生成
      newUser.createdAt = new Date().toISOString()
      newUser.avatar = '/src/assets/pictures/loginImages/default-avatar.png' // 设置默认头像

      // 保存用户
      users.push(newUser)
      saveUsersToStorage(users)

      resolve({message: '注册成功'})
    }, 1000) // 1秒延迟模拟网络请求
  })
}

/**
 * 用户登录
 * @param {string} username - 用户名
 * @param {string} password - 密码
 * @returns {Promise} 包含用户数据的Promise
 */
export const login = (username, password) => {
  return new Promise((resolve, reject) => {
    // 模拟网络延迟
    setTimeout(() => {
      const users = getUsersFromStorage()

      // 查找用户
      const user = users.find(u => 
        u.username === username && u.password === password
      )

      if (user) {
        // 登录成功，设置当前用户
        setCurrentUser(user)
        resolve({ user, message: '登录成功' })
      } else {
        // 登录失败
        reject({ message: '用户名或密码不正确' })
      }
    }, 1000) // 1秒延迟模拟网络请求
  })
}

/**
 * 检查用户是否已登录
 * @returns {Object|null} 当前登录的用户，如果未登录则返回null
 */
export const checkAuth = () => {
  return getCurrentUser()
}

/**
 * 用户退出登录
 */
export const userLogout = () => {
  logout()
  return { message: '已成功退出登录' }
}

/**
 * 删除用户账号
 * @param {string} userId - 用户ID
 * @returns {Promise} 包含成功/失败信息的Promise
 */
export const deleteUserAccount = (userId) => {
  return new Promise((resolve, reject) => {
    try {
      // 获取所有用户
      const users = getUsersFromStorage()

      // 查找用户索引
      const userIndex = users.findIndex(user => user.id === userId)

      if (userIndex === -1) {
        return reject({ message: '未找到用户账号' })
      }

      // 从用户列表中删除用户
      users.splice(userIndex, 1)

      // 保存更新后的用户列表
      saveUsersToStorage(users)

      // 如果删除的是当前登录的用户，则退出登录
      const currentUser = getCurrentUser()
      if (currentUser && currentUser.id === userId) {
        logout()
      }

      resolve({ message: '账号已成功注销' })
    } catch (error) {
      reject({ message: '注销账号失败，请稍后再试' })
    }
  })
}
