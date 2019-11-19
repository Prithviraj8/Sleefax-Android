package com.prithviraj8.copycatandroid;

public class UserInfo {
    public String name;
    public String email,device;
    public long num;



    public UserInfo(String name, String email,long num, String device){
        this.email = email;
        this.name = name;
        this.num = num;
        this.device = device;
    }

}
