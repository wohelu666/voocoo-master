package com.voocoo.pet.entity;

import java.io.Serializable;
import java.util.List;

public class WaitToDoList implements Serializable {

    public List<WaitToDo> waterToDoList;
    public List<WaitToDo> feederToDoList;

    public static class WaitToDo{
        public int deviceId;
        public String mac;
        public String deviceName;
        public String content;
        public String time;
    }
}
