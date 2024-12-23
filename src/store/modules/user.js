import { login, register, getUserInfo } from '@/api/auth'

const state = {
  token: localStorage.getItem('token'),
  userInfo: null
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_USER_INFO: (state, userInfo) => {
    state.userInfo = userInfo
  }
}

const actions = {
  // 登录
  login({ commit }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      login({ username: username.trim(), password: password })
        .then(response => {
          const { data } = response
          commit('SET_TOKEN', data.token)
          localStorage.setItem('token', data.token)
          resolve()
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 注册
  register({ commit }, userInfo) {
    const { username, password, email, userType } = userInfo
    return new Promise((resolve, reject) => {
      console.log('发送到后端的具体数据：', { username, password, email, userType })
      register({ username, password, email, userType })
        .then(response => {
          resolve(response)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 获取用户信息
  getUserInfo({ commit }) {
    return new Promise((resolve, reject) => {
      getUserInfo()
        .then(response => {
          const { data } = response
          console.log('获取到的用户信息：', data)
          console.log('用户类型：', data.userType)
          console.log('用户名：', data.username)
          commit('SET_USER_INFO', data)
          resolve(data)
        })
        .catch(error => {
          console.error('获取用户信息失败：', error)
          reject(error)
        })
    })
  },

  // 登出
  logout({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('SET_USER_INFO', null)
      localStorage.removeItem('token')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
} 