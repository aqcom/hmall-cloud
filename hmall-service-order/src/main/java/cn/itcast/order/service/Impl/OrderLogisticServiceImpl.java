package cn.itcast.order.service.Impl;
import cn.itcast.hmall.pojo.order.OrderLogistics;
import cn.itcast.order.mapper.OrderLogisticsMapper;
import cn.itcast.order.service.OrderLogisticService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(propagation = Propagation.REQUIRED ,readOnly = false)
public class OrderLogisticServiceImpl extends ServiceImpl<OrderLogisticsMapper, OrderLogistics> implements OrderLogisticService {
}
