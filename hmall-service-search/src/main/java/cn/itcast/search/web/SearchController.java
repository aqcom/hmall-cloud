package cn.itcast.search.web;

import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.search.SearchReqDTO;
import cn.itcast.hmall.pojo.item.ItemDoc;
import cn.itcast.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.huang
 * @version 1.0
 * @description TODO
 * @date 2023/3/1 21:01
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Resource
    SearchService searchService;

    @GetMapping("/suggestion")
    public List<String> suggest(@RequestParam("key") String key){
        try {
            return  searchService.suggest(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

/*    @PostMapping("filters")
    public Map<String,List<String>> searchFilter(@RequestBody SearchReqDTO dto){
        return searchService.searchFilter(dto);
    }*/

    @PostMapping("list")
    public PageDTO<ItemDoc> list(@RequestBody SearchReqDTO dto){
        return searchService.list(dto);
    }
}
