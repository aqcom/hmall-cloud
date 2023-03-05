package cn.itcast.search.listener;

import cn.itcast.hmall.dto.common.MqConstants;
import cn.itcast.search.service.SearchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Mr.huang
 * @version 1.0
 * @description TODO
 * @date 2023/3/2 1:17
 */
//@Component
/*public class MqListener {

    @Autowired
    SearchService searchService;

    @RabbitListener(queues = MqConstants.ITEM_INSERT_QUEUE)
    public void itemInsert(Long id){
        searchService.insert(id);
    }

    @RabbitListener(queues = MqConstants.ITEM_DELETE_QUEUE)
    public void itemDelete(Long id){
        searchService.delete(id);
    }

}*/
