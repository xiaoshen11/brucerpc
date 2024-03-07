package com.bruce.durpc.demo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @date 2024/3/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class  Order {

    private Long id;

    private Float amount;

}
