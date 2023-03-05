package cn.itcast.search.service.impl;


import cn.itcast.feign.client.ItemClient;
import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.common.ResultDTO;
import cn.itcast.hmall.dto.search.SearchReqDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.hmall.pojo.item.ItemDoc;
import cn.itcast.search.service.SearchService;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    ItemClient itemClient;

    @Resource
    RestHighLevelClient client;

    @Override
    public ResultDTO import2ES() {

        SearchReqDTO params = new SearchReqDTO();
        params.setPage(1);
        params.setSize(-1);

        PageDTO<Item> pageDTO = itemClient.list(params);

        List<Item> itemList = pageDTO.getList();

        Lists.partition(itemList, 1000).forEach(list -> {

            BulkRequest bulkRequest = new BulkRequest();

            try {

                for (Item item : list) {
                    // 将文档类型转换成ItemDoc
                    ItemDoc itemDoc = new ItemDoc(item);

                    // 创建request对象
                    bulkRequest.add(new IndexRequest("item")
                            .id(itemDoc.getId().toString())
                            .source(JSON.toJSONString(itemDoc), XContentType.JSON));
                }

                // 发送请求
                client.bulk(bulkRequest, RequestOptions.DEFAULT);

                log.info("success");

            } catch (IOException e) {

                throw new RuntimeException();

            }
        });
        return ResultDTO.ok();
    }

    @Override
    public List<String> suggest(String prefix) throws IOException {

        SearchRequest request=new SearchRequest("item");
        request.source().suggest(new SuggestBuilder()
                .addSuggestion("mySuggestion", SuggestBuilders.completionSuggestion("suggestion")
                        .prefix(prefix).skipDuplicates(true).size(10)));
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);

        CompletionSuggestion mySuggestion = search.getSuggest().getSuggestion("mySuggestion");
        List<CompletionSuggestion.Entry.Option> options = mySuggestion.getOptions();
        List<String> texts=new ArrayList<>();
        options.forEach(s->{
            String text = s.getText().toString();
            texts.add(text);
        });
        return texts;
    }
/*    private List<ItemDoc> handleResponse(SearchResponse response) {
        // 4.解析响应
        SearchHits searchHits = response.getHits();
        // 4.1.获取总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到" + total + "条数据");
        // 4.2.文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        List<ItemDoc> itemDocsList = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 获取文档source
            String json = hit.getSourceAsString();
            // 反序列化
            ItemDoc itemDoc = JSON.parseObject(json, ItemDoc.class);
            itemDocsList.add(itemDoc);

        }

        return itemDocsList;
    }*/

/*    @Override
    public Map<String, List<String>> searchFilter(SearchReqDTO dto) {
        try {
            SearchRequest request=new SearchRequest("item");

            baseQuery(dto, request);

            request.source().size(0).aggregation(AggregationBuilders.terms("categoryAgg").field("category").size(10));
            request.source().size(0).aggregation(AggregationBuilders.terms("brandAgg").field("brand").size(10));
            SearchResponse search = client.search(request, RequestOptions.DEFAULT);

            List<String> categoryAgg = getList(search, "categoryAgg");
            List<String> brandAgg = getList(search, "brandAgg");

            Map<String,List<String>> agg=new HashMap<>();
            agg.put("brand",brandAgg);
            agg.put("category",categoryAgg);


            return agg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    @Override
    public PageDTO<ItemDoc> list(SearchReqDTO dto) {
        try {
            SearchRequest request=new SearchRequest("item");
            baseQuery(dto, request);
            Integer page = dto.getPage();
            Integer size = dto.getSize();

            request.source().from((page - 1) * size).size(size);

            if (!"default".equals(dto.getSortBy())) {
                request.source().sort(dto.getSortBy(), SortOrder.DESC);
            }
            SearchResponse search = client.search(request, RequestOptions.DEFAULT);
            return parseJson(search);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * @description 数据同步-插入数据
     */
/*    @Override
    public void insert(Long id) {
        try {
            Item one = itemClient.getOne(id);
            ItemDoc itemDoc=new ItemDoc(one);
            IndexRequest request=new IndexRequest("item").id(id.toString());

            request.source(JSON.toJSONString(itemDoc), XContentType.JSON);
            client.index(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    /**
     * @description 数据同步-删除数据
     */
/*    @Override
    public void delete(Long id) {
        try {
            DeleteRequest request=new DeleteRequest("item").id(id.toString());

            client.delete(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

/*    private List<String> getList(SearchResponse search,String agg) {
        Terms categoryAgg = search.getAggregations().get(agg);

        List<? extends Terms.Bucket> buckets = categoryAgg.getBuckets();
        List<String> list=new ArrayList<>();
        buckets.forEach(s->{
            list.add(s.getKeyAsString());
        });
        return list;
    }*/

    PageDTO<ItemDoc> parseJson(SearchResponse searchResponse) {
        SearchHits hits = searchResponse.getHits();
        long total = hits.getTotalHits().value;
        SearchHit[] searchHits = hits.getHits();
        List<ItemDoc> itemDocs = new ArrayList<>();
        for (SearchHit searchHit : searchHits) {
            String string = searchHit.getSourceAsString();
            ItemDoc itemDoc = JSON.parseObject(string, ItemDoc.class);
//            Object[] sortValues = searchHit.getSortValues();
//            if (sortValues.length > 0) {
//                ItemDoc.setDistance(sortValues[0]);
//            }
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            if (!CollectionUtils.isEmpty(highlightFields)) {
                HighlightField highlightField = highlightFields.get("name");
                if (highlightField != null) {
                    itemDoc.setName(highlightField.getFragments()[0].toString());
                }
            }
            itemDocs.add(itemDoc);
        }
        PageDTO<ItemDoc> pageDTO=new PageDTO<>(total,itemDocs);

        return pageDTO;
    }

    /**
     * @description 基础查询
     */
    private void baseQuery(SearchReqDTO dto, SearchRequest request) {
        BoolQueryBuilder builder=QueryBuilders.boolQuery();
        if (org.springframework.util.StringUtils.isEmpty(dto.getKey())){
            builder.must(QueryBuilders.matchAllQuery());
        }else{
            builder.must(QueryBuilders.matchQuery("name", dto.getKey()));
        }
        if(!org.springframework.util.StringUtils.isEmpty(dto.getBrand())){
            builder.filter(QueryBuilders.termQuery("brand", dto.getBrand()));
        }
        if (!StringUtils.isEmpty(dto.getCategory())){
            builder.filter(QueryBuilders.termQuery("category", dto.getCategory()));
        }
        if(dto.getMinPrice()!=null){
            builder.filter(QueryBuilders.rangeQuery("price").gte(dto.getMinPrice()));
        }
        if(dto.getMaxPrice()!=null){
            builder.filter(QueryBuilders.rangeQuery("price").lte(dto.getMaxPrice()));
        }
        //算分控制
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(
                builder, new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{(
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                //过滤条件
                                QueryBuilders.termQuery("isAD", true),
                                //算分函数
                                ScoreFunctionBuilders.weightFactorFunction(10)
                        )

                )
                }).boostMode(CombineFunction.MULTIPLY);
        request.source().query(functionScoreQueryBuilder);

    }


/*    private void buildBasicQuery(SearchReqDTO params, SearchRequest request) {
        // create bool query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // keyword
        if (org.apache.commons.lang3.StringUtils.isNotBlank(params.getKey())) {
            boolQuery.must(QueryBuilders.matchQuery("all", params.getKey()));
        } else {
            boolQuery.must(QueryBuilders.matchAllQuery());
        }

        // brand
        if (org.apache.commons.lang3.StringUtils.isNotBlank(params.getBrand())) {
            boolQuery.filter(QueryBuilders.termQuery("brand", params.getBrand()));
        }

        // category
        if (org.apache.commons.lang3.StringUtils.isNotBlank(params.getCategory())) {
            boolQuery.filter(QueryBuilders.termQuery("category", params.getCategory()));
        }

        // price range
        if (params.getMinPrice() != null && params.getMaxPrice() != null) {
            boolQuery.filter(QueryBuilders
                    .rangeQuery("price")
                    .lte(params.getMaxPrice())
                    .gte(params.getMinPrice()));
        }

        request.source().query(boolQuery);

        // sort
        if (org.apache.commons.lang3.StringUtils.isNotBlank(params.getSortBy())) {
            if (params.getSortBy().equals("price")) {
                request.source().sort(params.getSortBy(), SortOrder.ASC);
            } else if (params.getSortBy().equals("sold")) {
                request.source().sort(params.getSortBy(), SortOrder.DESC);
            }
        }

    }*/


}
