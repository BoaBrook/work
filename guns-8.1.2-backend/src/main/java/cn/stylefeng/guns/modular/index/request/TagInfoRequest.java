package cn.stylefeng.guns.modular.index.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class TagInfoRequest {

    @NonNull
    private String modelId;

    private String systemType;

}
