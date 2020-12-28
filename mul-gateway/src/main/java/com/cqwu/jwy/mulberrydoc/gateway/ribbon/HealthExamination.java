package com.cqwu.jwy.mulberrydoc.gateway.ribbon;

import com.alibaba.fastjson.JSONObject;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

//@Component
public class HealthExamination implements IPing
{
    private static final Logger logger = LoggerFactory.getLogger(HealthExamination.class);

    @Override
    public boolean isAlive(Server server)
    {
        logger.info("当前serverId:\t{}", server.getId());
        try
        {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://" + server.getId() + "/ping").openConnection();
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
            logger.warn("当前serverId:\t{}不可用", server.getId());
        }
        return false;
    }
}
