package cn.itcast.search.service;

import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.common.ResultDTO;
import cn.itcast.hmall.dto.search.SearchReqDTO;
import cn.itcast.hmall.pojo.item.Item;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface EsService{

    /**
     * 搜索
     * @param params
     * @return
     */
    PageDTO search(SearchReqDTO params);


    /**
     * 搜索框提示自动补全
     * @param prefix 搜索关键字
     * @return
     */
    List<String> getSuggestions(String prefix);

    /**
     * 文本搜索内容跟过滤导航联动
     * @param params
     * @return
     */
//    Map<String, List<String>> getFilters(SearchReqDTO params);

    /**
        添加或全量修改es数据
        1). 先用id从mysql中查询数据
        2). 转换成json
        3). 再调用es的方法
        PUT hotel/_doc
        {
            json
        }
     */
//    void insertById(Long id);


}
