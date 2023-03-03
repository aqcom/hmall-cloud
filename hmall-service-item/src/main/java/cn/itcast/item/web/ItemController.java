package cn.itcast.item.web;


import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.common.ResultDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.item.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;


@RestController
@RequestMapping("item")
public class ItemController {


    @Autowired
    private ItemService itemService;


    @PostMapping("/list")
    public PageDTO<Item> pageQuery(@RequestBody SearchItemDTO request) {
        return itemService.pageQuery(request);
    }

    //根据id查询
    @GetMapping("/{itemId}")
    public Item getById(@PathVariable("itemId") Long itemId) {
        return itemService.getById(itemId);
    }

    //新增
    @PostMapping
    public boolean save(@RequestBody Item entity) {
        return itemService.save(entity);
    }

    //状态
    @PutMapping("/status/{id}/{status}")
    public ResultDTO updateByIdStatus(@PathVariable("id") Integer id,
                                      @PathVariable("status") Integer status) {

        return itemService.updateByIdStatus(id, status);
    }

    //修改
    public boolean update(@RequestBody Item item, Wrapper<Item> updateWrapper) {
        return itemService.update(item, updateWrapper);
    }

    //根据id删除
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable("id") Serializable id) {
        return itemService.removeById(id);

    }
}
