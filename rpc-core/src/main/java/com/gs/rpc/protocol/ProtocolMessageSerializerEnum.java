package com.gs.rpc.protocol;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
* 协议消息的序列化器枚举
* @Param:
* @return:
* @Date: 2024/4/16
*/
@Getter
public enum ProtocolMessageSerializerEnum {

    JDK(0,"jdk"),
    JSON(1,"json"),
    KRYO(2,"kryo"),
    HESSIAN(3,"hessian");


    /**
     * key数字，用于ProtocolMessage头进行紧凑传输
     */
    private final int key;

    /**
     * String，“jdk”通过名称
     */
    private final String value;

    ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
    * 获取值列表
    * @Param:
    * @return: java.util.List<java.lang.String>
    * @Date: 2024/4/16
    */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
    * 根据key获取枚举
    * @Param: [key]
    * @return: com.gs.rpc.protocol.ProtocolMessageSerializerEnum
    * @Date: 2024/4/16
    */

    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
