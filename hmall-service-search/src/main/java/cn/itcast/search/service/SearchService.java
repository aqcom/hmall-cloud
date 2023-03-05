package cn.itcast.search.service;

import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.common.ResultDTO;
import cn.itcast.hmall.dto.search.SearchReqDTO;
import cn.itcast.hmall.pojo.item.ItemDoc;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SearchService {

    ResultDTO import2ES();

    List<String> suggest(String prefix) throws IOException;

//    Map<String, List<String>> searchFilter(SearchReqDTO dto);

    PageDTO<ItemDoc> list(SearchReqDTO dto);

//    void insert(Long id);

//    void delete(Long id);


}
