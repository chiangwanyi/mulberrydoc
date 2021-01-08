package com.cqwu.jwy.mulberrydoc.common.configure;

import java.util.Objects;

/**
 * 服务实例信息
 */
public abstract class ServerInstance
{
    /**
     * 实例ID
     */
    private String instanceId;

    public String getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerInstance instance = (ServerInstance) o;
        return Objects.equals(instanceId, instance.instanceId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(instanceId);
    }
}
