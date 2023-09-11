package com.mao.milvus.demo.service;

import com.mao.milvus.demo.vo.ReplyMsg;
import com.mao.milvus.demo.vo.SearchParamVo;
import com.mao.milvus.demo.vo.SearchResultVo;
import io.milvus.param.dml.InsertParam;

import java.util.List;

public interface MilvusService {
    void loadCollection(String collectionName);
    void releaseCollection(String collectionName);
    void loadPartitions(String collectionName, List<String> partitionsName);
    void releasePartitions(String collectionName, List<String> partitionsName);
    boolean isExitCollection(String collectionName);
    Boolean creatCollection(String collectionName);
    Boolean dropCollection(String collectionName);
    void createPartition(String collectionName, String partitionName);
    Boolean createIndex(String collectionName);
    ReplyMsg insert(String collectionName, List<InsertParam.Field> fields);
    List<List<SearchResultVo>> searchTopKSimilarity(SearchParamVo contentParamVo);
}
