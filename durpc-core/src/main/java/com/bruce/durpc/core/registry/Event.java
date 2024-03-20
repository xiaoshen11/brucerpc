package com.bruce.durpc.core.registry;

import com.bruce.durpc.core.meta.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @date 2024/3/17
 */
@Data
@AllArgsConstructor
public class Event {

    List<InstanceMeta> data;

}
