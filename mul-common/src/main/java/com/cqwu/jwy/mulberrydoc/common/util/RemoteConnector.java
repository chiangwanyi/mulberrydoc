package com.cqwu.jwy.mulberrydoc.common.util;

import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 远程调用连接器
 */
public class RemoteConnector
{
    /** LOG */
    private static final Logger LOG = LoggerFactory.getLogger(RemoteConnector.class);
    /** HTTP */
    private static final String HTTP = "http://";
    /** RestTemplate */
    private RestTemplate restTemplate;

    public RemoteConnector(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    /**
     * 组合 URL
     *
     * @param service 服务名
     * @param method  方法名
     * @return URL
     */
    public static String url(String service, String method)
    {
        return HTTP + String.format("%s/%s", service, method);
    }

    /**
     * GET请求 - 无参
     *
     * @param service 服务名
     * @param method  方法名
     * @return HttpResponse
     */
    public HttpResponse get(String service, String method)
    {
        return get(service, method, HttpResponse.class);
    }

    /**
     * GET请求 - 无参 - 自定义返回值
     *
     * @param service      服务名
     * @param method       方法名
     * @param responseType 返回值类型
     * @param <T>          泛型
     * @return 返回值
     */
    public <T> T get(String service, String method, Class<T> responseType)
    {
        try
        {
            return restTemplate.getForObject(url(service, method), responseType);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * POST请求
     *
     * @param service 服务名
     * @param method  方法名
     * @param obj     传入参数
     * @return HttpResponse
     */
    public HttpResponse post(String service, String method, Object obj)
    {
        return post(service, method, obj, HttpResponse.class);
    }

    /**
     * POST请求 - 自定义返回值
     *
     * @param service      服务名
     * @param method       方法名
     * @param obj          传入参数
     * @param responseType 返回值类型
     * @param <T>          泛型
     * @return 返回值
     */
    public <T> T post(String service, String method, Object obj, Class<T> responseType)
    {
        try
        {
            return restTemplate.postForObject(url(service, method), obj, responseType);
        }
        catch (Exception e)
        {
            LOG.error("请求时发生错误，error：", e);
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
