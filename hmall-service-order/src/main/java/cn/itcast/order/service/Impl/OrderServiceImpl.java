package cn.itcast.order.service.Impl;

import cn.itcast.order.service.OrderService;


import cn.itcast.feign.client.ItemClient;
import cn.itcast.feign.client.UserClient;
import cn.itcast.hmall.dto.common.ResultDTO;
import cn.itcast.hmall.dto.common.ThreadLocalUtil;
import cn.itcast.hmall.dto.order.OrderReqDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.hmall.pojo.order.Order;
import cn.itcast.hmall.pojo.order.OrderDetail;
import cn.itcast.hmall.pojo.order.OrderLogistics;
import cn.itcast.hmall.pojo.user.Address;
import cn.itcast.hmall.pojo.user.User;
import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.service.OrderDetailService;
import cn.itcast.order.service.OrderLogisticService;
import cn.itcast.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderLogisticService orderLogisticService;
    @Transactional(propagation = Propagation.REQUIRED ,readOnly = false)
    @Override
    public String addOrder(OrderReqDTO dto) {
        // 1. 远程查询对应商品信息
        Item one = itemClient.getOne(dto.getItemId());
        Long totalFee=one.getPrice()*dto.getNum();
        int status =1;
        Order order = new Order();
        order.setStatus(status);
        order.setTotalFee(totalFee);
        order.setPaymentType(dto.getPaymentType());
        Address addressID = userClient.getAddressID(dto.getAddressId());
        order.setUserId(addressID.getUserId());
        //插入订单
        this.save(order);
        // 2. 插入订单描述信息
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(order.getId());
        orderDetail.setItemId(one.getId());
        orderDetail.setNum(dto.getNum());
        orderDetail.setName(one.getName());
        orderDetail.setPrice(one.getPrice());
        orderDetail.setSpec(one.getSpec());
        orderDetail.setImage(one.getImage());
        orderDetailService.save(orderDetail);
        // 3. 保存物流信息
        OrderLogistics orderLogistics = new OrderLogistics();
        orderLogistics.setOrderId(order.getId());
        orderLogistics.setContact(addressID.getContact());
        orderLogistics.setMobile(addressID.getMobile());
        orderLogistics.setProvince(addressID.getProvince());
        orderLogistics.setCity(addressID.getCity());
        orderLogistics.setTown(addressID.getTown());
        orderLogistics.setStreet(addressID.getStreet());
        orderLogisticService.save(orderLogistics);
        // 4.调用商品服务扣减库存
        itemClient.stock(one.getId(),dto.getNum());
        return String.valueOf(order.getId());
    }

    //支付
    @Transactional(propagation = Propagation.REQUIRED ,readOnly = false)
    @Override
    public ResultDTO pay(Long orderId, String password) {
        if(orderId==null|| !StringUtils.isNotBlank(password)){
            throw  new RuntimeException("出错");
        }
        Order order = this.getById(orderId);
        if(order.getStatus()!=1){
            throw new RuntimeException("支付状态有问题");
        }
        Long userId = ThreadLocalUtil.getUserId();
        User user = userClient.getUser(userId);
        ResultDTO resultDTO = new ResultDTO();
        if(!user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
            throw  new RuntimeException("密码错误");
        }
        userClient.pay(order.getTotalFee(),userId);
        UpdateWrapper<Order> orderUpdateWrapper = new UpdateWrapper<>();
        orderUpdateWrapper.eq("id",orderId).set("status",2);
        boolean update = this.update(orderUpdateWrapper);
        resultDTO.setSuccess(update);
        resultDTO.setMsg("成功");
        return resultDTO;
    }
}
