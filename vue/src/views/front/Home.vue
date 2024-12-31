<template>
  <div class="main-content">
    <div style="display: flex">
      <div class="left"></div>
      <div style="width: 120%; background-color: white; margin-bottom: 50px;margin-top: 40px;border-radius: 20px">
        <div style="margin: 40px 0 0 15px; height: 40px; background-color: #04BF04FF; font-size: 20px; color: white; width: 130px; font-weight: bold; line-height: 40px; text-align: center; border-radius: 20px">主题市场</div>
        <div style="display: flex; margin: 0 25px; height: 550px">
          <div style="flex: 2">
            <div style="display: flex; color: #666666FF; margin: 14px 0" v-for="item in typeData">
              <img :src="item.img" alt="" style="height: 20px; width: 20px">
              <div style="margin-left: 10px; font-size: 15px"><a href="#" @click="navTo('/front/type?id=' + item.id)">{{item.name}}</a></div>
            </div>
          </div>
          <div style="flex: 5; margin-top: 15px">
            <div>
              <el-carousel height="300px" style="border-radius: 10px">
                <el-carousel-item v-for="item in carousel_top">
                  <img :src="item" alt="" style="width: 90%; height: 350px; border-radius: 10px">
                </el-carousel-item>
              </el-carousel>
            </div>
            <div style="margin-top: 30px; display: flex">
              <div style="flex: 1">
                <el-carousel height="300px" style="border-radius: 10px">
                  <el-carousel-item v-for="item in carousel_left">
                    <img :src="item" alt="" style="width: 100%; height: 200px; border-radius: 10px">
                  </el-carousel-item>
                </el-carousel>
              </div>
              <div style="flex: 1; margin-left: 5px">
                <el-carousel height="300px" style="border-radius: 10px">
                  <el-carousel-item v-for="item in carousel_right">
                    <img :src="item" alt="" style="width: 100%; height: 200px; border-radius: 10px">
                  </el-carousel-item>
                </el-carousel>
              </div>
            </div>
          </div>
          <div style="flex: 3; background-color: #F3F3F3FF; margin-top: 15px; margin-left: 15px; border-radius: 10px">

            <div style="margin-top: 20px; padding: 0 15px">
              <img src="@/assets/imgs/shu10.jpg" alt="" style="height: 150px; width: 100%; border-radius: 20px">
            </div>
            <div style="margin: 40px 10px 10px 10px; width: 250px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis">
              <i class="el-icon-bell"></i>
              <span style="font-weight: bold">公告</span>
              <span style="color: #666666;">：{{ top }}</span>
            </div>
            <div style="display: flex; margin-top: 70px">
              <div style="flex: 1; text-align: center">
                <a href="#" @click="navTo('/front/collect')">
                  <img src="@/assets/imgs/收藏.png" alt="" style="height: 25px; width: 25px">
                  <div>我的收藏</div>
                </a>
              </div>
              <div style="flex: 1; text-align: center">
                <a href="#" @click="navTo('/front/address')">
                  <img src="@/assets/imgs/店铺.png" alt="" style="height: 25px; width: 25px">
                  <div>我的地址</div>
                </a>
              </div>
              <div style="flex: 1; text-align: center">
                <a href="#" @click="navTo('/front/cart')">
                  <img src="@/assets/imgs/购物车.png" alt="" style="height: 25px; width: 25px">
                  <div>我的购物车</div>
                </a>
              </div>
              <div style="flex: 1; text-align: center">
                <a href="#" @click="navTo('/front/orders')">
                  <img src="@/assets/imgs/订单.png" alt="" style="height: 25px; width: 25px">
                  <div>我的订单</div>
                </a>
              </div>
            </div>
          </div>
        </div>

        <div class="card-container">

          <a href="https://baidu.com" target="_blank" class="ad-link">
            <div class="ad-card" >
              <div class="media-container" >
                <!-- 根据素材类型显示不同内容 -->
                <!-- 图片广告 -->
                <img src="https://web-andrew.oss-cn-shanghai.aliyuncs.com/materials/2024/12/26/ad1e9e3d-fcfb-46d6-862d-59b0e3d13d43.avif"
                class="media-content"
                alt="好一个广告">

                <!-- 视频广告 -->


                <!-- 文字广告 -->

              </div>
              <div class="ad-info">
                <h3 class="ad-title">好一个广告</h3>
                <p class="ad-description">百度新闻</p >
              </div>
            </div>
          </a >


          <!-- 无广告内容时显示 -->

        </div>
        <div style="margin: 40px 0 0 15px; height: 40px; background-color: #04BF04FF; font-size: 20px; color: white; width: 130px; font-weight: bold; line-height: 40px; text-align: center; border-radius: 20px">热卖商品</div>
        <div style="margin: 10px 5px 0 5px">
          <el-row>
            <el-col :span="5" v-for="item in goodsData">
              <img @click="navTo('/front/detail?id=' + item.id)" :src="item.img" alt="" style="width: 100%; height: 210px; border-radius: 10px; border: #cccccc 1px solid">
              <div style="margin-top: 10px; font-weight: 500; font-size: 16px; width: 180px; color: #000000FF; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">{{item.name}}</div>
              <div style="margin-top: 5px; font-size: 20px; color: #FF5000FF">￥ {{item.price}} / {{item.unit}}</div>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="right"></div>
    </div>
  </div>
</template>

<script>

export default {

  data() {
    return {
      //user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      typeData: [],
      top: null,
      notice: [],
      goodsData: [],
      recommendData: [],
      carousel_top: [
        require('@/assets/imgs/shu15.png'),
        require('@/assets/imgs/shu16.png'),
        require('@/assets/imgs/shu17.png'),
        require('@/assets/imgs/shu18.jpg'),
      ],
      carousel_left: [
        require('@/assets/imgs/shu4.jpg'),
        require('@/assets/imgs/shu5.jpg'),
        require('@/assets/imgs/shu6.jpg'),
      ],
      carousel_right: [
        require('@/assets/imgs/shu3.jpg'),
        require('@/assets/imgs/shu8.jpg'),
        require('@/assets/imgs/shu14.png'),
      ],
    }
  },
  mounted() {
    this.loadType()
    this.loadNotice()
    this.loadGoods()

  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadType() {
      this.$request.get('/type/selectAll').then(res => {
        if (res.code === '200') {
          this.typeData = res.data
          console.log(res.data)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadNotice() {
      this.$request.get('/notice/selectAll').then(res => {
        this.notice = res.data
        let i = 0
        if (this.notice && this.notice.length) {
          this.top = this.notice[0].content
          setInterval(() => {
            this.top = this.notice[i].content
            i++
            if (i === this.notice.length) {
              i = 0
            }
          }, 2500)
        }
      })
    },
    loadGoods() {
      this.$request.get('/goods/selectTop15').then(res => {
        if (res.code === '200') {
          this.goodsData = res.data
          console.log(res.data)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    navTo(url) {
      location.href = url
    },
  }
}
</script>

<style scoped>
.main-content {
  min-height: 100vh;
  /*overflow: hidden;*/
  background-size: 200%;
  background-image: url('@/assets/imgs/beijing10.png');
}
.left {
  width: 17%;
  background-repeat: no-repeat;

}
.right {
  width: 17%;
  height: auto;
  background-repeat: no-repeat;

}
.el-col-5{
  width: 20%;
  max-width: 20%;
  padding: 10px 10px;
}

</style>