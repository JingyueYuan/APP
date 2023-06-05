package com.jkf.graduateproject.Enum;

public enum respCode{
    /*


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
