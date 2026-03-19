package cn.stylefeng.guns.modular.industrialTV.request;

import lombok.Data;

@Data
public class ControlPtzRequest {

    private String deviceId;

    //left, right, up, down, upleft, upright, downleft, downright, zoomin, zoomout, stop
    private String command;

}
