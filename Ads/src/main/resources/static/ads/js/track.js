(function() {
    // 生成唯一设备ID
    function generateDeviceId() {
        let deviceId = localStorage.getItem('device_id');
        if (!deviceId) {
            deviceId = 'dev_' + Math.random().toString(36).substr(2, 9);
            localStorage.setItem('device_id', deviceId);
        }
        return deviceId;
    }

    // 发送跟踪数据
    function sendTrackingData(data) {
        fetch('/track', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).catch(console.error);
    }

    // 等待Vue应用加载完成
    function waitForApp() {
        if (window.Vue) {
            // 创建Vue混入
            window.Vue.mixin({
                mounted() {
                    if (this.$options.name === 'Cart') {
                        this.$nextTick(() => {
                            const originalPay = this.pay;
                            this.pay = function(...args) {
                                if (this.selectedData && this.selectedData.length > 0) {
                                    const trackingData = {
                                        device_id: generateDeviceId(),
                                        items: this.selectedData.map(item => ({
                                            name: item.goodsName,
                                            quantity: item.num
                                        }))
                                    };
                                    sendTrackingData(trackingData);
                                }
                                return originalPay.apply(this, args);
                            };
                        });
                    }
                }
            });
        } else {
            // ��果Vue还没加载完成，等待100ms后重试
            setTimeout(waitForApp, 100);
        }
    }

    // 开始等待Vue加载
    waitForApp();
})();

/*数据格式
* {
  "device_id": "dev_abc123xyz",
  "items": [
    {
      "name": "商品名称",
      "quantity": 2
    },
    {
      "name": "商品名称2",
      "quantity": 1
    }
  ]
}*/