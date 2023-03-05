package cn.itcast.feign.client;
import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.dto.search.SearchReqDTO;
import cn.itcast.hmall.pojo.item.Item;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient("itemservice")
public interface ItemClient{
    /**
     * 查询商品列表
     * @param params
     * @return
     */
    @GetMapping("/item/list")
    public PageDTO<Item> list(@RequestBody SearchReqDTO params);

    /**
     * 根据id 查询商品信息
     * @param id
     * @return
     */
    @GetMapping("/item/{id}")
    public Item getOne(@PathVariable("id")Long id);

    /**
     * 扣减商品库存
     * @param itemId
     * @param num
     */
    @PutMapping("/item/stock/{itemId}/{num}")
    public void stock(@PathVariable("itemId") Long itemId ,@PathVariable("num")Integer num);

    /**
     * 增加商品库存
     * @param id
     * @param num
     */
    @RequestMapping("/item/add/{id}/{num}")
    void add(@PathVariable("id") Long id,@PathVariable("num")Integer num);
}
