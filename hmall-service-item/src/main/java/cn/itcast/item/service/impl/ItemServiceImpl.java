package cn.itcast.item.service.impl;


import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.common.ResultDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.item.mapper.ItemMapper;
import cn.itcast.item.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {
    @Autowired
    private ItemMapper itemMapper;
    @Override
    public ResultDTO updateByIdStatus(Integer id, Integer status) {
        return null;
    }

    @Override
    public PageDTO<Item> pageQuery(SearchItemDTO request) {
        LambdaQueryWrapper<Item> wrapper = Wrappers.lambdaQuery();
        queryParams(request, wrapper);
        IPage<Item> page = new Page<>(request.getPage(), request.getSize());
        itemMapper.selectPage(page, wrapper);
        PageDTO<Item> pageDTO = new PageDTO<>();
        pageDTO.setTotal(page.getTotal());
        pageDTO.setList(page.getRecords());
        return pageDTO;
    }

    private void queryParams(SearchItemDTO request, LambdaQueryWrapper<Item> wrapper) {
        if (StringUtils.isNotBlank(request.getName())) {
            wrapper.eq(Item::getName, request.getName());
        }
        if (StringUtils.isNotBlank(request.getBrand())) {
            wrapper.eq(Item::getBrand, request.getBrand());
        }

        if (StringUtils.isNotBlank(request.getCategory())) {
            wrapper.eq(Item::getCategory, request.getCategory());
        }

        if (request.getBeginDate() != null && request.getEndDate() != null) {
            wrapper.between(
                    Item::getCreateTime,
                    request.getBeginDate(),
                    request.getEndDate());
        }


    }

}
