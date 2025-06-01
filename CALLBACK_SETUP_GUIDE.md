# 微信支付回调地址配置指南

## 🔗 回调地址：https://www.juncaishe.com/notify

## 📋 配置步骤

### 1. 域名解析配置
确保 `www.juncaishe.com` 域名正确解析到你的服务器IP地址：

```bash
# 检查域名解析
nslookup www.juncaishe.com

# 或使用ping测试
ping www.juncaishe.com
```

### 2. 服务器环境配置

#### 2.1 Nginx反向代理配置
在你的服务器上配置Nginx，将微信回调请求转发到Spring Boot应用：

```nginx
# /etc/nginx/sites-available/juncaishe.com
server {
    listen 443 ssl http2;
    server_name www.juncaishe.com juncaishe.com;
    
    # SSL证书配置
    ssl_certificate /path/to/your/ssl/certificate.crt;
    ssl_certificate_key /path/to/your/ssl/private.key;
    
    # 微信支付回调接口
    location /notify {
        proxy_pass http://127.0.0.1:8080/notify;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 保持请求体不变，微信回调需要原始数据
        proxy_pass_request_body on;
        proxy_pass_request_headers on;
    }
    
    # 支付管理界面
    location /payment {
        proxy_pass http://127.0.0.1:8080/payment;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # 健康检查
    location /notify/health {
        proxy_pass http://127.0.0.1:8080/notify/health;
    }
}

# HTTP重定向到HTTPS
server {
    listen 80;
    server_name www.juncaishe.com juncaishe.com;
    return 301 https://$server_name$request_uri;
}
```

#### 2.2 防火墙配置
确保服务器防火墙允许HTTPS流量：

```bash
# Ubuntu/Debian
sudo ufw allow 443
sudo ufw allow 80

# CentOS/RHEL
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

### 3. SSL证书配置

**必须使用HTTPS**，微信支付回调只支持HTTPS协议。

#### 方法1：使用Let's Encrypt免费证书
```bash
# 安装certbot
sudo apt-get install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d www.juncaishe.com -d juncaishe.com
```

#### 方法2：购买商业SSL证书
将证书文件上传到服务器并在Nginx中配置。

### 4. Spring Boot应用部署

#### 4.1 打包应用
```bash
# 在payment-manager目录下
./gradlew build -x test

# 生成的jar文件位置
# build/libs/payment-manager-1.0.0.jar
```

#### 4.2 部署到服务器
```bash
# 上传jar文件到服务器
scp build/libs/payment-manager-1.0.0.jar user@your-server:/opt/payment-manager/

# 上传证书文件
scp -r ../api_Certificate user@your-server:/opt/payment-manager/
```

#### 4.3 运行应用
```bash
# 在服务器上运行
cd /opt/payment-manager
nohup java -jar payment-manager-1.0.0.jar > app.log 2>&1 &

# 或使用systemd服务（推荐）
```

#### 4.4 创建systemd服务文件
```bash
# /etc/systemd/system/payment-manager.service
[Unit]
Description=Payment Manager Application
After=network.target

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/payment-manager
ExecStart=/usr/bin/java -jar payment-manager-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务：
```bash
sudo systemctl daemon-reload
sudo systemctl enable payment-manager
sudo systemctl start payment-manager
sudo systemctl status payment-manager
```

### 5. 回调地址验证

#### 5.1 测试回调地址可达性
```bash
# 测试健康检查接口
curl -X GET https://www.juncaishe.com/notify/health

# 应该返回："微信支付回调服务正常运行"
```

#### 5.2 测试回调接口
```bash
# 测试回调接口
curl -X POST https://www.juncaishe.com/notify/test \
  -H "Content-Type: application/json" \
  -d '{"test": "data"}'

# 应该返回："测试回调接收成功"
```

#### 5.3 检查日志
```bash
# 查看应用日志
tail -f /opt/payment-manager/app.log

# 或使用systemd日志
sudo journalctl -u payment-manager -f
```

## 🔧 在微信商户平台配置回调地址

### 1. 登录微信商户平台
访问：https://pay.weixin.qq.com/

### 2. 配置支付回调URL
1. 进入"开发配置" → "网站应用"
2. 找到你的应用(AppID: wx3840944fac88bd49)
3. 设置支付回调URL：`https://www.juncaishe.com/notify`

### 3. 配置退款回调URL（可选）
设置退款回调URL：`https://www.juncaishe.com/notify/refund`

## 🚨 重要注意事项

### 1. 网络安全
- **必须使用HTTPS**
- 确保SSL证书有效且未过期
- 建议使用TLS 1.2或更高版本

### 2. 服务器稳定性
- 确保服务器24/7可访问
- 配置自动重启机制
- 监控服务器资源使用情况

### 3. 回调处理要求
- 必须在**5秒内**返回响应
- 成功处理返回`"SUCCESS"`
- 失败处理返回`"FAIL"`
- 微信会重试失败的回调（最多3次）

### 4. IP白名单（可选）
微信支付回调的IP段：
```
101.227.200.0/24
101.227.204.0/24
```

可以在Nginx中配置IP白名单：
```nginx
location /notify {
    allow 101.227.200.0/24;
    allow 101.227.204.0/24;
    deny all;
    
    proxy_pass http://127.0.0.1:8080/notify;
    # ... 其他配置
}
```

## ✅ 验证检查清单

- [ ] 域名解析正确指向服务器
- [ ] SSL证书配置正确，HTTPS可访问
- [ ] Nginx反向代理配置正确
- [ ] Spring Boot应用正常运行在8080端口
- [ ] 防火墙允许80和443端口
- [ ] 健康检查接口可访问：`https://www.juncaishe.com/notify/health`
- [ ] 测试回调接口可访问：`https://www.juncaishe.com/notify/test`
- [ ] 微信商户平台已配置回调URL
- [ ] 服务器日志正常，无错误信息

## 🔍 故障排查

### 问题1：域名无法访问
```bash
# 检查域名解析
nslookup www.juncaishe.com
# 检查服务器网络
ping 服务器IP
```

### 问题2：SSL证书问题
```bash
# 测试SSL证书
openssl s_client -connect www.juncaishe.com:443
# 检查证书有效期
echo | openssl s_client -connect www.juncaishe.com:443 2>/dev/null | openssl x509 -noout -dates
```

### 问题3：应用无法启动
```bash
# 检查Java环境
java -version
# 检查端口占用
netstat -tulpn | grep 8080
# 查看应用日志
tail -f app.log
```

### 问题4：微信回调失败
1. 检查回调URL是否正确配置
2. 确认服务器可以接收POST请求
3. 查看应用日志中的回调处理记录
4. 验证签名验证是否正确

---

**配置完成后，你的支付管理系统将能够：**
- ✅ 接收微信支付回调通知
- ✅ 自动更新订单支付状态
- ✅ 提供Web管理界面
- ✅ 支持JSAPI和Native支付 