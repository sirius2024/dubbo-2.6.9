/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.rpc.cluster.loadbalance;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;

import java.util.List;
import java.util.Random;

/**
 * random load balance.
 * 该算法主要就是从服务列表invokers里面随机选择一个，这个算法也是dubbo默认的负载均衡算法（如果你不配置loadbalance来指定使用哪种算法，将会使用随机算法），当然该算法不仅仅使用random随机一个集合的index，返回index对应invoker，还使用了权重，用权重计算随机。
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "random";

    private final Random random = new Random();

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        // 总个数
        int length = invokers.size(); // Number of invokers
        // 总权重
        int totalWeight = 0; // The sum of weights
        // 权重是否都一样
        boolean sameWeight = true; // Every invoker has the same weight?
        for (int i = 0; i < length; i++) {
            //计算权重
            int weight = getWeight(invokers.get(i), invocation);
            // 累计总权重
            totalWeight += weight; // Sum
            // 计算所有权重是否一样
            //若不为第0个，则将当前权重与上一个进行比较,只要有一个不等,则认为不等sameWeight = false;
            if (sameWeight && i > 0 && weight != getWeight(invokers.get(i - 1), invocation)) {
                sameWeight = false;
            }
        }

        //当拥有不同权重的invoker的时候 总权重>0且sameWeight=false
        if (totalWeight > 0 && !sameWeight) {
            // 如果权重不相同且权重大于0则按总权重数随机
            // If (not every invoker has the same weight & at least one invoker's weight>0), select randomly based on totalWeight.
            int offset = random.nextInt(totalWeight);
            // Return a invoker based on the random value.
            // 并确定随机值落在哪个片断上
            for (int i = 0; i < length; i++) {
                //权重越大，随机数减去小于0的概率越大
                offset -= getWeight(invokers.get(i), invocation);
                if (offset < 0) {
                    return invokers.get(i);
                }
            }
        }

        //如果权重相同，则是真随机
        // 如果权重相同或权重为0则均等随机
        // If all invokers have the same weight value or totalWeight=0, return evenly.
        return invokers.get(random.nextInt(length));
    }

}
