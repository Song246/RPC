package com.gs.rpc.protocol;

import lombok.Getter;

/**
* 协议消息的类型枚举(消息解码)
* @Param: 
* @return: 
* @Date: 2024/4/16
*/
@Getter
public enum ProtocolMessageTypeEnum {

    REQUEST(0),
    RESPONSE(1),
    HEARTBEAT(2),
    OTHERS(3);

    /**
     * 放在ProtocolMessage头部紧凑传输
     */
    private final int key;

    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }

    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum anEnum : ProtocolMessageTypeEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }

}
