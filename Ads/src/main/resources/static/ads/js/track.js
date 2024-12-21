(function() {
    // 生成唯一的设备ID
    function generateDeviceId() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            const r = Math.random() * 16 | 0;
            const v = c === 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }

    // 获取或创建设备ID
    function getOrCreateDeviceId() {
        let deviceId = localStorage.getItem('device_id');
        if (!deviceId) {
            deviceId = generateDeviceId();
            localStorage.setItem('device_id', deviceId);
        }
        return deviceId;
    }

    // 设置第三方cookie
    function setCrossDomainCookie(deviceId) {
        // 创建一个隐藏的iframe
        const iframe = document.createElement('iframe');
        iframe.style.display = 'none';
        // 通过URL参数传递deviceId
        iframe.src = `http://localhost:8081/api/setCookie?deviceId=${deviceId}`;

        // 添加到文档中
        document.body.appendChild(iframe);

        // 一段时间后移除iframe
        setTimeout(() => {
            document.body.removeChild(iframe);
        }, 1000);
    }

    // 发送购物数据到广告网站
    function sendPurchaseData(purchaseData) {
        const deviceId = getOrCreateDeviceId();

        // 设置第三方cookie
        setCrossDomainCookie(deviceId);

        // 发送购物数据
        fetch('http://localhost:8081/api/track', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                deviceId: deviceId,
                purchases: purchaseData
            }),
            mode: 'cors'
        }).catch(console.error);
    }

    // 注入追踪代码到Vue组件
    const originalPay = Vue.prototype.$pay;
    Vue.prototype.$pay = function(data) {
        if (originalPay) {
            originalPay.call(this, data);
        }

        // 发送购买数据
        const purchaseData = data.cartData.map(item => ({
            goodsName: item.goodsName,
            quantity: item.num,
        }));

        sendPurchaseData(purchaseData);
    };
})();