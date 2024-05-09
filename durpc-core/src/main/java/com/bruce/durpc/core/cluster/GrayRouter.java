package com.bruce.durpc.core.cluster;

import com.bruce.durpc.core.api.Router;
import com.bruce.durpc.core.meta.InstanceMeta;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 灰度路由
 *
 * @date 2024/3/31
 */
public class GrayRouter implements Router<InstanceMeta> {

    @Getter
    @Setter
    private int grayRatio = -1;
    private final Random random = new Random();



    public GrayRouter(int grayRatio) {
        this.grayRatio = grayRatio;
    }

    @Override
    public List<InstanceMeta> route(List<InstanceMeta> providers) {
        if(providers == null || providers.size() <= 1){
            return providers;
        }

        List<InstanceMeta> normalNodes = new ArrayList<>();
        List<InstanceMeta> grayNodes = new ArrayList<>();

        providers.forEach( p ->{
            if("true".equals(p.getParameters().get("gray"))){
                grayNodes.add(p);
            } else {
                normalNodes.add(p);
            }
        });

        if(normalNodes.isEmpty() || grayNodes.isEmpty()){
            return providers;
        }

        if (grayRatio <= 0) {
            return normalNodes;
        } else if(grayRatio >= 100) {
            return grayNodes;
        }

        if(random.nextInt(100) < grayRatio){
            return grayNodes;
        }else {
            return normalNodes;
        }
    }
}
