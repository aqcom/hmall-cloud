package cn.itcast.order.service.Impl;

import cn.itcast.hmall.pojo.order.OrderDetail;
import cn.itcast.order.mapper.OrderDetailMapper;
import cn.itcast.order.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED ,readOnly = false)
public class OrderDetailServiceImpl  extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
