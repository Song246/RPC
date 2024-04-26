package com.gs.rpc.protocol;

import lombok.Getter;

/**
* 协议消息的枚举状态（请求成功、失败；响应失败）
* @Param: 
* @return: 
* @Date: 2024/4/16
*/
@Getter
public enum ProtocolMessageStatusEnum {

    OK("ok",20),
    BAD_REQUEST("bad_request",40),
    BAD_RESPONSE("bad_response",50);

    /**
     *
     */
    private final String text;

    /**
     *
     */
    private final int value;

    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }


    /** 
    * 根据val获取枚举
    * @Param: [value]
    * @return: com.gs.rpc.protocol.ProtocolMessageStatusEnum
    * @Date: 2024/4/16
    */
    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        for (ProtocolMessageStatusEnum anEnum : ProtocolMessageStatusEnum.values()) {
            if (anEnum.value==value) {
                return anEnum;
            }
        }
        return null;
    }




}
