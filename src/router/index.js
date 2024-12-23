import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/components/common/Layout'
import store from '@/store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/register'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index'),
        meta: { title: '首页' }
      },
      // 广告主路由
      {
        path: '/advertiser',
        component: () => import('@/views/advertiser/layout'),
        redirect: '/advertiser/ad',
        meta: { title: '广告主' },
        children: [
          {
            path: 'ad',
            name: 'AdManagement',
            component: () => import('@/views/advertiser/ad/index'),
            meta: { title: '广告管理' }
          },
          {
            path: 'ad/test',
            name: 'AdApiTest',
            component: () => import('@/views/advertiser/ad/test'),
            meta: { title: '广告接口测试' }
          },
          {
            path: 'material',
            name: 'MaterialManagement',
            component: () => import('@/views/advertiser/material/index'),
            meta: { title: '素材管理' }
          },
          {
            path: 'delivery',
            name: 'DeliveryManagement',
            component: () => import('@/views/advertiser/delivery/index'),
            meta: { title: '投放管理' }
          },
          {
            path: 'statistics',
            name: 'AdvertiserStatistics',
            component: () => import('@/views/advertiser/statistics/index'),
            meta: { title: '数据统计' }
          }
        ]
      },
      // 网站主路由
      {
        path: '/publisher',
        component: () => import('@/views/publisher/layout'),
        redirect: '/publisher/website',
        meta: { title: '网站主' },
        children: [
          {
            path: 'website',
            name: 'WebsiteManagement',
            component: () => import('@/views/publisher/website/index'),
            meta: { title: '网站管理' }
          },
          {
            path: 'adspace',
            name: 'AdSpaceManagement',
            component: () => import('@/views/publisher/adspace/index'),
            meta: { title: '广告位管理' }
          },
          {
            path: 'statistics',
            name: 'PublisherStatistics',
            component: () => import('@/views/publisher/statistics/index'),
            meta: { title: '收益统计' }
          }
        ]
      },
      // 管理员路由
      {
        path: '/admin',
        component: () => import('@/views/admin/layout'),
        redirect: '/admin/user',
        meta: { title: '管理员' },
        children: [
          {
            path: 'user',
            name: 'UserManagement',
            component: () => import('@/views/admin/user/index'),
            meta: { title: '用户管理' }
          },
          {
            path: 'ad/review',
            name: 'AdReview',
            component: () => import('@/views/admin/ad/review'),
            meta: { title: '广告审核' }
          },
          {
            path: 'website/review',
            name: 'WebsiteReview',
            component: () => import('@/views/admin/website/review'),
            meta: { title: '网站审核' }
          },
          {
            path: 'adspace/review',
            name: 'AdSpaceReview',
            component: () => import('@/views/admin/adspace/review'),
            meta: { title: '广告位审核' }
          }
        ]
      }
    ]
  }
]

const router = new VueRouter({
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '广告投放平台'

  // 获取token
  const token = localStorage.getItem('token')
  // 获取用户信息
  const userInfo = store.state.user.userInfo

  // 白名单路由
  const whiteList = ['/login', '/register']

  if (token) {
    if (to.path === '/login') {
      next('/')
    } else {
      if (!userInfo) {
        try {
          // 如果没有用户信息，则获取用户信息
          await store.dispatch('user/getUserInfo')
          next()
        } catch (error) {
          // 如果获取用户信息失败，则登出并跳转到登录页
          await store.dispatch('user/logout')
          next('/login')
        }
      } else {
        next()
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router 