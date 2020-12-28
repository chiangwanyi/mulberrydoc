package com.cqwu.jwy.mulberrydoc.gateway.ribbon;

import com.alibaba.fastjson.JSONObject;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyRoundRule extends AbstractLoadBalancerRule
{
    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(MyRoundRule.class);

    /** 当前轮询的Server */
    private AtomicInteger nextServerCyclicCounter;
    /** 定时任务 */
    private ScheduledExecutorService retryService;
    /** 等待重新检测的Server Map */
    private Map<String, Server> retryServers;

    @PostConstruct
    private void init()
    {
        nextServerCyclicCounter = new AtomicInteger(0);
        retryServers = new ConcurrentHashMap<>();
        retryService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        retryService.scheduleWithFixedDelay(new RetryExecutor(), 10, 5, TimeUnit.SECONDS);
    }

    /**
     * 选择正常的Server
     *
     * @param lb  LoadBalancer
     * @param key key
     * @return Server
     */
    public Server choose(ILoadBalancer lb, Object key)
    {
        if (lb == null)
        {
            LOG.warn("no load balancer");
            return null;
        }

        List<Server> allServers = lb.getAllServers();
        LOG.info("当前所有的Server:");
        allServers.forEach(s -> LOG.info("server:\t{}", s.getId()));
        int serverCount = allServers.size();

        // 没有server
        if (serverCount == 0)
        {
            LOG.warn("当前无Server");
            return null;
        }

        Server reachableServer = getReachableServer(allServers);
        if (Objects.isNull(reachableServer))
        {
            LOG.warn("当前无可用Server");
            return null;
        }
        return reachableServer;
    }

    /**
     * 获取连接成功的server
     *
     * @param allServers 所有server
     * @return 连接成功的server
     */
    private Server getReachableServer(List<Server> allServers)
    {
        // 最多尝试 allServers.size() 次
        for (int i = 0; i < allServers.size(); i++)
        {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % allServers.size();
            if (nextServerCyclicCounter.compareAndSet(current, next))
            {
                Server server = allServers.get(next);
                // 检查服务是否已被标记为不可用
                if (retryServers.containsKey(server.getId()))
                {
                    LOG.info("Server:[{}]暂时不可用，等待重新检测", server.getId());
                    continue;
                }
                // 检查服务是否可用
                if (isAlive(server))
                {
                    LOG.info("Server:[{}]可用", server.getId());
                    return server;
                }
                // 标记不可用服务
                else
                {
                    retryServers.putIfAbsent(server.getId(), server);
                    LOG.warn("Server:[{}]不可用，将在10秒后重新检查", server.getId());
                }
            }
        }
        return null;
    }

    /**
     * 检查Server连接状态
     *
     * @param server 服务
     * @return 检查结果
     */
    public boolean isAlive(Server server)
    {
        LOG.info("开始测试 Server:[{}] 连接状态", server.getId());
        try
        {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://" + server.getId() + "/ping").openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
            reader.close();
            JSONObject jsonObject = JSONObject.parseObject(builder.toString());
            for (Map.Entry<String, Object> entry : jsonObject.entrySet())
            {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (Objects.equals("status", key))
                {
                    Integer code = (Integer) value;
                    if (code.equals(200))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }

    class RetryExecutor implements Runnable
    {
        @Override
        public void run()
        {
            LOG.info("开始检查待重试Servers");
            if (!retryServers.isEmpty())
            {
                Set<String> removeServer = new HashSet<>();
                for (Map.Entry<String, Server> entry : retryServers.entrySet())
                {
                    String serverId = entry.getKey();
                    Server server = entry.getValue();
                    LOG.info("开始重新检测 Server:{}", serverId);
                    if (isAlive(server))
                    {
                        LOG.info("Server:{} 可用", serverId);
                        removeServer.add(serverId);
                    }
                    else
                    {
                        LOG.info("Server:{} 仍不可用", serverId);
                    }
                }
                for (String serverId : removeServer)
                {
                    retryServers.remove(serverId);
                }
            }
            else
            {
                LOG.info("当前无待重试Server");
            }
        }
    }

    @Override
    public Server choose(Object key)
    {
        return choose(getLoadBalancer(), key);
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig)
    {
    }
}
