package com.wechat.pay.java.service;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse;

/** JSAPI 下单为例 */
public class QuickStart {

  /** 商户号 */
  public static String merchantId = "1683311162";

  /** 商户API私钥路径 */
  // 请确保 api_Certificate 目录在项目的根目录下，并且包含您的商户私钥文件 apiclient_key.pem
  public static String privateKeyPath = "api_Certificate/apiclient_key.pem";

  /** 商户证书序列号 */
  public static String merchantSerialNumber = "35E63A455062980FA300C820CB2E0AF03E3F6A45";

  /** 商户APIV3密钥 */
  public static String apiV3Key = "O7pL9kQ2rS5uV8wX3yZ4aB6cD1eF0gHj";

  public static void main(String[] args) {
    // 使用自动更新平台证书的RSA配置
    // 一个商户号只能初始化一个配置，否则会因为重复的下载任务报错
    Config config =
        new RSAAutoCertificateConfig.Builder()
            .merchantId(merchantId)
            .privateKeyFromPath(privateKeyPath)
            .merchantSerialNumber(merchantSerialNumber)
            .apiV3Key(apiV3Key)
            .build();
    JsapiService service = new JsapiService.Builder().config(config).build();
    // request.setXxx(val)设置所需参数，具体参数可见Request定义
    PrepayRequest request = new PrepayRequest();
    Amount amount = new Amount();
    amount.setTotal(100);
    request.setAmount(amount);
    request.setAppid("wxa9d9651ae******");
    request.setMchid("190000****");
    request.setDescription("测试商品标题");
    request.setNotifyUrl("https://notify_url");
    request.setOutTradeNo("out_trade_no_001");
    Payer payer = new Payer();
    payer.setOpenid("oLTPCuN5a-nBD4rAL_fa********");
    request.setPayer(payer);
    PrepayResponse response = service.prepay(request);
    System.out.println(response.getPrepayId());
  }
}
