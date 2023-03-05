package cn.itcast.order.service;

import cn.itcast.hmall.dto.common.ResultDTO;
import cn.itcast.hmall.dto.order.OrderReqDTO;
import cn.itcast.hmall.pojo.order.Order;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<Order> {
    String addOrder(OrderReqDTO dto);
    ResultDTO pay(Long orderId, String password);
}

