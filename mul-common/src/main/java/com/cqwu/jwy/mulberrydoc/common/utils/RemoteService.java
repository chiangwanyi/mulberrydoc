package com.cqwu.jwy.mulberrydoc.common.utils;

import org.springframework.web.client.RestTemplate;

public class RemoteService
{
    /** HTTP */
    private static final String HTTP = "http://";
    /** RestTemplate */
    private RestTemplate restTemplate;

    public RemoteService(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    /**
     * 组合 URL
     *
     * @param service 服务名
     * @param mapping 路径
     * @return URL
     */
    public static String url(String service, String mapping)
    {
        return HTTP + String.format("%s/%s", service, mapping);
    }

    /**
     * 调用远程服务 - GET
     *
     * @param service      服务名
     * @param mapping      路径
     * @param responseType 返回值类
     * @param <T>          返回值类型
     * @return 返回值
     */
    public <T> T call(String service, String mapping, Class<T> responseType)
    {
        try
        {
            return restTemplate.getForObject(url(service, mapping), responseType);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public RestTemplate getRestTemplate()
    {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }
}
