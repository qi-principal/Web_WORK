import axios from 'axios';


// 创建一个新的axios对象
const request = axios.create({
    baseURL: process.env.VUE_APP_BASEURL,
    timeout: 30000
});

// request 拦截器
request.interceptors.request.use(
    config => {
        config.headers['Content-Type'] = 'application/json;charset=utf-8'; // 设置请求头格式

        return config;
    },
    error => {
        console.error('request error: ' + error); // for debug
        return Promise.reject(error);
    }
);

// response 拦截器
request.interceptors.response.use(
    response => {
        let res = response.data;

        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res;
        }
        return res;
    },
    error => {
        console.error('response error: ' + error); // for debug
        return Promise.reject(error);
    }
);

export default request;