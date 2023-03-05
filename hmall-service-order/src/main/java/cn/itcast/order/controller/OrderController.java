package cn.itcast.order.controller;


import cn.itcast.hmall.dto.common.ResultDTO;
import cn.itcast.hmall.dto.order.OrderReqDTO;
import cn.itcast.hmall.pojo.order.Order;
import cn.itcast.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @PostMapping("/order")
    public String getId(@RequestBody OrderReqDTO dto){
        return   orderService.addOrder(dto);
    }
    @PutMapping("/pay/{id}")
    public ResultDTO pay(@PathVariable("id") Long orderId , @RequestBody OrderReqDTO dto){
        return orderService.pay(orderId,dto.getPassword());
    }
    @GetMapping("/order/{id}")
    public Order getOrder(@PathVariable("id")Long orderId){
        return orderService.getById(orderId);
    }

}
