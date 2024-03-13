package com.jkf.graduateproject.Enum;

public enum respCode{
    /*

    123 -- 表示注册成功
    456 -- 注册失败
    789 -- 用户已经存在
    135 -- 表示上传成功
    246 -- 表示图片回调成功
    147 -- 登录成功
    258 -- 登录失败（密码账户不对）
    369 -- 此账户未进行注册
    159 -- 密码修改成功
    357 -- 密码修改失败
     */
    SUCCESS("123"), FAIL("456"), ALLWAYS("789"),LOGIN("147"),LOGINFAIL("258"),
    LOGINUNRE("369"),EDITPWFAIL("357"),EDITPWSUCC("159");

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private  respCode(String code){
        this.code = code;
    }
}
