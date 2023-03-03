package cn.itcast.item.service;


import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.common.ResultDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.pojo.item.Item;
import com.baomidou.mybatisplus.extension.service.IService;


public interface ItemService extends IService<Item> {
    ResultDTO updateByIdStatus(Integer id,Integer status);

    PageDTO<Item> pageQuery(SearchItemDTO request);
}
