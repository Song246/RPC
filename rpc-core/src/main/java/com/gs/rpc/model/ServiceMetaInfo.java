package com.gs.rpc.model;

import cn.hutool.core.util.StrUtil;
import com.gs.rpc.constant.RpcConstant;
import lombok.Data;

/**
 * 服务元信息（注册信息）
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-10 20:16
 **/
@Data
public class ServiceMetaInfo {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion= RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口
     */
    private Integer servicePort;

    /**
     * 服务分组（未实现）
     */
    private String serviceGroup="default";


    /**
    * 获取服务键名，格式：  服务名：版本号
    * @Param: []
    * @return: java.lang.String
    * @Date: 2024/4/10
    */

    public String getServiceKey() {
        // 后续扩展分组   服务名:服务版本:分组
        // return String.format("%s:%s:%s",serviceName,serviceVersion,serviceGroup);
        return String.format("%s:%s",serviceName,serviceVersion);
    }

    /** 
    * 获取服务注册节点键名   格式   服务键名/host地址：端口号
    * @Param: []
    * @return: java.lang.String
    * @Date: 2024/4/10
    */
    
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s",getServiceKey(),serviceHost,servicePort);
    }

    /**
    * 获取完整服务地址
    * @Param: []
    * @return: java.lang.String
    * @Date: 2024/4/11
    */
    public String getServiceAddress() {
        if(!StrUtil.contains(serviceHost,"http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);

    }

}
