package com.mao.milvus.demo.service.impl;

import com.mao.milvus.demo.service.MilvusService;
import com.mao.milvus.demo.utils.Content;
import com.mao.milvus.demo.vo.ReplyMsg;
import com.mao.milvus.demo.vo.SearchParamVo;
import com.mao.milvus.demo.vo.SearchResultVo;
import io.milvus.client.MilvusClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.GetIndexBuildProgressResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.GetIndexBuildProgressParam;
import io.milvus.param.partition.CreatePartitionParam;
import io.milvus.param.partition.LoadPartitionsParam;
import io.milvus.param.partition.ReleasePartitionsParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@Slf4j
public class MilvusServiceImpl implements MilvusService {
    @Resource
    private MilvusClient milvusClient;
    @Override
    public void loadCollection(String collectionName) {
        LoadCollectionParam loadCollectionParam = LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        R<RpcStatus> response = milvusClient.loadCollection(loadCollectionParam);
        log.info("loadCollection {} is {}", collectionName, response.getData().getMsg());
    }

    @Override
    public void releaseCollection(String collectionName) {
        ReleaseCollectionParam param = ReleaseCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        R<RpcStatus> response = milvusClient.releaseCollection(param);
        log.info("releaseCollection {} is {}", collectionName, response.getData().getMsg());
    }

    @Override
    public void loadPartitions(String collectionName, List<String> partitionsName) {
        LoadPartitionsParam build = LoadPartitionsParam.newBuilder()
                .withCollectionName(collectionName)
                .withPartitionNames(partitionsName)
                .build();
        R<RpcStatus> rpcStatusR = milvusClient.loadPartitions(build);
        log.info("loadPartitions {} is {}", partitionsName, rpcStatusR.getData().getMsg());
    }

    @Override
    public void releasePartitions(String collectionName, List<String> partitionsName) {
        ReleasePartitionsParam build = ReleasePartitionsParam.newBuilder()
                .withCollectionName(collectionName)
                .withPartitionNames(partitionsName)
                .build();
        R<RpcStatus> rpcStatusR = milvusClient.releasePartitions(build);
        log.info("releasePartition {} is {}", collectionName, rpcStatusR.getData().getMsg());
    }

    @Override
    public boolean isExitCollection(String collectionName) {
        HasCollectionParam hasCollectionParam = HasCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        R<Boolean> response = milvusClient.hasCollection(hasCollectionParam);
        Boolean isExists = response.getData();
        log.info("collection {} is exists: {}", collectionName, isExists);
        return isExists;
    }

    @Override
    public Boolean creatCollection(String collectionName) {
        // 主键字段
        FieldType fieldType1 = FieldType.newBuilder()
                .withName(Content.Field.ID)
                .withDescription("primary key")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();
        // 文本字段
        FieldType fieldType2 = FieldType.newBuilder()
                .withName(Content.Field.CONTENT)
                .withDataType(DataType.VarChar)
                .withMaxLength(Content.MAX_LENGTH)
                .build();
        // 向量字段
        FieldType fieldType3 = FieldType.newBuilder()
                .withName(Content.Field.CONTENT_VECTOR)
                .withDataType(DataType.FloatVector)
                .withDimension(Content.FEATURE_DIM)
                .build();
        // 创建collection
        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDescription("Schema of Content")
                .withShardsNum(Content.SHARDS_NUM)
                .addFieldType(fieldType1)
                .addFieldType(fieldType2)
                .addFieldType(fieldType3)
                .build();
        R<RpcStatus> response = milvusClient.createCollection(createCollectionReq);

        log.info("collection: {} is created ? status: = {}", collectionName, response.getData().getMsg());
        return response.getData().getMsg().equals("Success");
    }

    @Override
    public Boolean dropCollection(String collectionName) {
        DropCollectionParam book = DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        R<RpcStatus> response = milvusClient.dropCollection(book);
        return response.getData().getMsg().equals("Success");
    }

    @Override
    public void createPartition(String collectionName, String partitionName) {
        CreatePartitionParam param = CreatePartitionParam.newBuilder()
                .withCollectionName(collectionName)
                .withPartitionName(partitionName)
                .build();
        R<RpcStatus> partition = milvusClient.createPartition(param);
        String msg = partition.getData().getMsg();
        log.info("create partition: {} in collection: {} is: {}", partition, collectionName, msg);
    }

    @Override
    public Boolean createIndex(String collectionName) {
        // IndexType
        final IndexType INDEX_TYPE = IndexType.AUTOINDEX;
        // ExtraParam 建议值为 4 × sqrt(n), 其中 n 指 segment 最多包含的 entity 条数。
        final String INDEX_PARAM = "{\"nlist\":16384}";
        long startIndexTime = System.currentTimeMillis();
        R<RpcStatus> response = milvusClient.createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withIndexName(Content.CONTENT_INDEX)
                .withFieldName(Content.Field.CONTENT_VECTOR)
                .withIndexType(INDEX_TYPE)
                .withMetricType(MetricType.L2)
                .withExtraParam(INDEX_PARAM)
                .withSyncMode(Boolean.TRUE)
                .withSyncWaitingInterval(500L)
                .withSyncWaitingTimeout(30L)
                .build());
        long endIndexTime = System.currentTimeMillis();
        log.info("Succeed in " + (endIndexTime - startIndexTime) / 1000.00 + " seconds!");
        log.info("createIndex --->>> {} ", response.toString());
        GetIndexBuildProgressParam build = GetIndexBuildProgressParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        R<GetIndexBuildProgressResponse> idnexResp = milvusClient.getIndexBuildProgress(build);
        log.info("getIndexBuildProgress --->>> {}", idnexResp.getStatus());

        return response.getData().getMsg().equals("Success");
    }

    @Override
    public ReplyMsg insert(String collectionName, List<InsertParam.Field> fields) {
        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields)
                .build();
        R<MutationResult> mutationResultR = milvusClient.insert(insertParam);

        log.info("Flushing...");
        long startFlushTime = System.currentTimeMillis();
        milvusClient.flush(FlushParam.newBuilder()
                .withCollectionNames(Collections.singletonList(collectionName))
                .withSyncFlush(true)
                .withSyncFlushWaitingInterval(50L)
                .withSyncFlushWaitingTimeout(30L)
                .build());
        long endFlushTime = System.currentTimeMillis();
        log.info("Succeed in " + (endFlushTime - startFlushTime) / 1000.00 + " seconds!");

        if (mutationResultR.getStatus() == 0){
            long insertCnt = mutationResultR.getData().getInsertCnt();
            log.info("Successfully! Total number of entities inserted: {} ", insertCnt);
            return ReplyMsg.ofSuccess("success", insertCnt);
        }
        log.error("InsertRequest failed!");
        return ReplyMsg.ofErrorMsg("InsertRequest failed!");
    }

    @Override
    public List<List<SearchResultVo>> searchTopKSimilarity(SearchParamVo searchParamVo) {
        log.info("------search TopK Similarity------");
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(searchParamVo.getCollectionName())
                .withMetricType(MetricType.L2)
                .withOutFields(searchParamVo.getOutputFields())
                .withTopK(searchParamVo.getTopK())
                .withVectors(searchParamVo.getQueryVectors())
                .withVectorFieldName(Content.Field.CONTENT_VECTOR)
                .withParams(searchParamVo.getParams())
                .build();

        R<SearchResults> respSearch = milvusClient.search(searchParam);
        if (respSearch.getData() == null) {
            return null;
        }
        log.info("------ process query results ------");
        SearchResultsWrapper wrapper = new SearchResultsWrapper(respSearch.getData().getResults());
        List<List<SearchResultVo>> result = new ArrayList<>();
        for (int i = 0; i < searchParamVo.getQueryVectors().size(); ++i) {
            List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(i);
            List<QueryResultsWrapper.RowRecord> rowRecords = wrapper.getRowRecords();
            List<SearchResultVo> list = new ArrayList<>();
            for (int j = 0; j < scores.size(); ++j) {
                SearchResultsWrapper.IDScore score = scores.get(j);
                QueryResultsWrapper.RowRecord rowRecord = rowRecords.get(j);
                long longID = score.getLongID();
                float distance = score.getScore();
                String content = (String) rowRecord.get(searchParamVo.getOutputFields().get(0));
                log.info("Top " + j + " ID:" + longID + " Distance:" + distance);
                log.info("Content: " + content);
                list.add(SearchResultVo.builder().id(longID).score(distance).conent(content).build());
            }
            result.add(list);
        }
        log.info("Successfully!");

        return result;
    }
}
