package cn.stylefeng.guns.modular.accesscontrol.request;

import lombok.Data;

import java.util.List;

@Data
public class AccessControlGatewayRequest {
    private List<String> deviceIds;
    /*命令值：0- 关闭（对于梯控，表示受控），1- 打开（对于梯控，表示开门），2- 常开（对于梯控，表示自由、通道状态），
            3- 常关（对于梯控，表示禁用），4- 恢复（梯控，普通状态），5- 访客呼梯（梯控），6- 住户呼梯（梯控）*/
    private int command;
}
