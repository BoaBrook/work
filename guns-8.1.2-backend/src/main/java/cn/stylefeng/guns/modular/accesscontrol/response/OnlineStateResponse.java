package cn.stylefeng.guns.modular.accesscontrol.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineStateResponse {

    private int totalNum = 0;
    private int onlineNum = 0;
}
