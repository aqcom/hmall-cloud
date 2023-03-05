package cn.itcast.order.job;

import cn.itcast.feign.client.ItemClient;
import cn.itcast.hmall.pojo.order.Order;
import cn.itcast.hmall.pojo.order.OrderDetail;
import cn.itcast.order.service.OrderDetailService;
import cn.itcast.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName 类名
 * @Description 类说明
 */
@Slf4j
@Component
public class OrderJob {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ItemClient itemClient;

    @XxlJob("cancelOrder")
    public ReturnT cancelOrder(String params){
        log.info(">>>>>>>>>>>>取消订单定时任务执行<<<<<<<<<<<<");
        LambdaQueryWrapper<Order> wrapper = Wrappers.<Order>lambdaQuery();
        // 查询未付款订单
        wrapper.eq(Order::getStatus,1);
        List<Order> list = orderService.list(wrapper);

        // 遍历未付款订单
        list.stream().filter(order -> {
            return (System.currentTimeMillis() - order.getCreateTime().getTime()) > 120000; // 演示效果 大于2分钟
        }).map(order -> {
            // 将订单状态改为取消
            order.setStatus(5);
            return order;
        }).forEach(order -> {
            // 修改订单为取消状态
            orderService.updateById(order);

            // 根据订单id查询关联的订单详情信息
            List<OrderDetail> orderDetailList = orderDetailService.list(Wrappers.<OrderDetail>lambdaQuery().eq(OrderDetail::getOrderId, order.getId()));
            for (OrderDetail orderDetail : orderDetailList) {
                // 补库存
                itemClient.add(orderDetail.getItemId(),orderDetail.getNum());
            }
        });
        return ReturnT.SUCCESS;
    }
}
