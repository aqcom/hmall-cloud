package cn.itcast.search.service.impl;


import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.search.SearchReqDTO;
import cn.itcast.hmall.pojo.item.ItemDoc;
import cn.itcast.search.service.EsService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class EsServiceImpl implements EsService{

    @Autowired
    private RestHighLevelClient client;

    @Override
    public PageDTO search(SearchReqDTO params) {
        try {
            //1. 请求对象
            SearchRequest searchRequest = new SearchRequest("item");
            //2. 请求数据
            //基本请求
            basicQuery(params,searchRequest);
            //分页
            int page = 1;
            int size = 10;
            if(params.getPage() != null && params.getSize() != null){
                page = params.getPage();
                size = params.getSize();
            }
            searchRequest.source()
                    .from((page-1)*size)
                    .size(size);
            //3. 发起请求
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            //4. 解析响应,封装成PageResult
            PageDTO pageResult = parseResponse(response);
            return pageResult;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private PageDTO parseResponse(SearchResponse response) {

        SearchHits searchHits = response.getHits();
        //总文档数
        long total = searchHits.getTotalHits().value;
        //文档集合
        SearchHit[] hits = searchHits.getHits();
        ArrayList<ItemDoc> list = new ArrayList<>();
        if(hits != null && hits.length > 0){
            for (SearchHit hit : hits) {
                String json = hit.getSourceAsString();
                ItemDoc itemDoc = JSON.parseObject(json, ItemDoc.class);
                list.add(itemDoc);
            }
        }

        return new PageDTO(total,list);
    }

    private void basicQuery(SearchReqDTO params, SearchRequest searchRequest) {

        SearchSourceBuilder source = searchRequest.source();
        //复合搜索
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        //如果没有搜索关键字,就查询所有,否则根据关键字搜索
        if(StringUtils.isBlank(params.getKey())){
            builder.must(QueryBuilders.matchAllQuery());
        }else{
            builder.must(QueryBuilders.matchQuery("all",params.getKey()));
        }
//        builder.must();
//        builder.filter();
        if(!StringUtils.isBlank(params.getCategory())){
            builder.filter(QueryBuilders.termQuery("category",params.getCategory()));
        }
        if(!StringUtils.isBlank(params.getBrand())){
            builder.filter(QueryBuilders.termQuery("brand",params.getBrand()));
        }
        if(params.getMinPrice() != null){
            builder.filter(QueryBuilders.rangeQuery("price").gte(params.getMinPrice()));
        }
        if(params.getMaxPrice() != null){
            builder.filter(QueryBuilders.rangeQuery("price").lte(params.getMaxPrice()));
        }

        // 2.算分控制
        FunctionScoreQueryBuilder functionScoreQuery =
                QueryBuilders.functionScoreQuery(
                        // 原始查询，相关性算分的查询
                        builder,
                        // function score的数组
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                                // 其中的一个function score 元素
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                        // 过滤条件
                                        QueryBuilders.termQuery("isAD", true),
                                        // 算分函数
                                        ScoreFunctionBuilders.weightFactorFunction(10)
                                )
                                //默认是相乘
                        }).boostMode(CombineFunction.MULTIPLY);

        source.query(functionScoreQuery);
    }
    /**
     * 搜索框提示自动补全
     * @param prefix 搜索关键字
     * @return
     */
    @Override
    public List<String> getSuggestions(String prefix) {
        try {
            // 1.准备Request
            SearchRequest request = new SearchRequest("item");
            // 2.准备DSL
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "itemsuggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10)
            ));
            // 3.发起请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 4.解析结果
            Suggest suggest = response.getSuggest();
            // 4.1.根据补全查询名称，获取补全结果
            CompletionSuggestion suggestions = suggest.getSuggestion("itemsuggestions");
            // 4.2.获取options
            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
            // 4.3.遍历
            List<String> list = new ArrayList<>(options.size());
            for (CompletionSuggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                list.add(text);
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

/*    @Override
    public Map<String, List<String>> getFilters(SearchReqDTO params) {
        try {
            //1. 请求对象
            SearchRequest request = new SearchRequest("hotel");
            //2. 请求数据
            //先构建查询
            basicQuery(params,request);
            //后聚合
            SearchSourceBuilder source = request.source();
            source.size(0);
            source.aggregation(
                    AggregationBuilders.terms("brandAgg")
                            .field("brand")
                            .size(10)
            );
            source.aggregation(
                    AggregationBuilders.terms("cityAgg")
                            .field("city")
                            .size(10)
            );
            source.aggregation(
                    AggregationBuilders.terms("starNameAgg")
                            .field("starName")
                            .size(10)
            );
            //3. 发送请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            //4. 解析响应
            Map<String,List<String>> map = parseAggResponse(response);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, List<String>> parseAggResponse(SearchResponse response) {
        Aggregations aggregations = response.getAggregations();
        List<String> brandList = getStringListMap(aggregations,"brandAgg");
        List<String> cityList = getStringListMap(aggregations,"cityAgg");
        List<String> starNameList = getStringListMap(aggregations,"starNameAgg");
        //key不能乱写
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("city",cityList);
        map.put("brand",brandList);
        map.put("starName",starNameList);
        return map;
    }*/

/*    @Resource
    private ItemClient itemClient;*/

/*    @Override
    public void insertById(Long id) {
        try {
            //查询mysql数据库,获取指定id的数据
            Item item = itemClient.getOne(id);
            //类型转换: mysql实体类转成es实体类
            ItemDoc hotelDoc = new ItemDoc(item);
            //json转换: fastjson
            String json = JSON.toJSONString(hotelDoc);

            //1. 创建请求对象
            IndexRequest indexRequest = new IndexRequest("item");
            indexRequest.id(hotelDoc.getId() + "");
            //2. 设置请求参数
            indexRequest.source(json, XContentType.JSON);
            //3. 发送请求
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            System.out.println(response.getResult());//第一次是created,再次是updated
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
