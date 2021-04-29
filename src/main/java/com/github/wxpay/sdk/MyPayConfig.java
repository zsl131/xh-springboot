package com.github.wxpay.sdk;

import com.zslin.business.mini.model.MiniConfig;

import java.io.*;

public class MyPayConfig extends WXPayConfig {

    private byte[] certData;

    private MiniConfig config;

    /**
     * 构造函数
     * @param certPath .p12证书路径
     * @param config 小程序配置对象
     */
    public MyPayConfig(String certPath, MiniConfig config) {
        try {
            File file = new File(certPath);
            InputStream certStream = new FileInputStream(file);
            this.certData = new byte[(int) file.length()];
            certStream.read(this.certData);
            certStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.config = config;
    }

    @Override
    public String getAppID() {
        return config.getAppid();
    }

    @Override
    public String getMchID() {
        return config.getMchid();
    }

    @Override
    public String getKey() {
        return config.getApiKey();
    }

    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    @Override
    IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {
            }

            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo("api.mch.weixin.qq.com", false);
            }
        };
    }
}
