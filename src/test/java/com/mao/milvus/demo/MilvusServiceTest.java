package com.mao.milvus.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mao.milvus.demo.client.ChatClient;
import com.mao.milvus.demo.client.embeddings.EmbeddingData;
import com.mao.milvus.demo.client.embeddings.EmbeddingResponse;
import com.mao.milvus.demo.service.MilvusService;
import com.mao.milvus.demo.utils.Content;
import com.mao.milvus.demo.vo.SearchParamVo;
import com.mao.milvus.demo.vo.SearchResultVo;
import io.milvus.client.MilvusClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
public class MilvusServiceTest {
    @Resource
    MilvusService milvusService;
    @Resource
    MilvusClient milvusClient;
    @Resource
    ChatClient chatClient;
    @Test
    void isExitCollection() {
        boolean mediumArticles = milvusService.isExitCollection("medium_articles");
        Assertions.assertTrue(mediumArticles);
    }


    @Test
    void creatCollection() {
        Boolean created = milvusService.creatCollection(Content.COLLECTION_NAME);
        Assertions.assertTrue(created);
    }

    @Test
    void createIndex(){
        Boolean index = milvusService.createIndex(Content.COLLECTION_NAME);
        Assertions.assertTrue(index);
    }

    @Test
    void insert() {
        String str = "唐尚珺，来自于广西。36岁的他已经参加了15次高考，这一次他还是放弃了上大学的机会。有人认为，步入中年的他应该选择上大学，否则他已经没有机会了，幸运之神不可能总是光顾他。也有人认为，唐尚珺除了上大学还有其他的出路，比如直播，他是高考复读出了名的，也因此获得了许多人的关注，他完全可以直播卖货，不比上大学差。上大学并不是唯一的出路。";
        List<String> content = Collections.singletonList(str);
        // 获得向量
        EmbeddingResponse embeddings = chatClient.embeddings(str);
        List<Float> vector = embeddings.getData().get(0).getEmbedding();
        List<List<Float>> vectors = Collections.singletonList(vector);
        // 准备插入向量数据库
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(Content.Field.CONTENT, content));
        fields.add(new InsertParam.Field(Content.Field.CONTENT_VECTOR, vectors));
        // 执行操作
        milvusService.insert(Content.COLLECTION_NAME, fields);
    }

    @Test
    public void insertVector(){
        List<String> sentenceList = new ArrayList<>();
        sentenceList.add("记者9月2日从广东湛江海事局了解到，鉴于台风“苏拉”较大可能于3日早晨前后以热带风暴级别再次登陆湛江，目前，湛江已启动防热带气旋Ⅱ级应急响应。");
        sentenceList.add("为应对水上突发事件，海事部门提前部署海上救援力量，根据台风动态更新社会应急力量布点，在琼州海峡北岸、湛江港霞山港区、海湾大桥、宝钢湛江钢铁基地对开水域、雷州半岛流沙湾等重要部位，部署大马力拖轮和海上专业救助船共9艘，确保能第一时间投入抢险救援。");
        sentenceList.add("9月2日14时至3日14时，受“苏拉”影响，南海西北部、北部湾东部、琼州海峡及广东中西部沿海、广西南部沿海、海南北部沿海、珠江口以及香港和澳门等地将有6-8级、阵风9-10级的大风，“苏拉”中心经过的附近海面或地区风力有11-12级、阵风13-14级；受“海葵”影响，台湾海峡、巴士海峡、浙江南部沿海、福建沿海、广东东部沿海、台湾岛沿海将有6-8级，阵风9-10级大风，台湾以东洋面部分海域风力可达9-12级，阵风13-14级，“海葵”中心经过的附近海面风力可达13-16级，阵风17级及以上。中央气象台9月2日10时继续发布台风红色预警。");
        // 1.获得向量
        EmbeddingResponse embeddings = chatClient.embeddings(sentenceList);
        List<List<Float>> vectors = embeddings.getData().stream().map(EmbeddingData::getEmbedding).toList();
        // 2.准备插入向量数据库
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(Content.Field.CONTENT, sentenceList));
        fields.add(new InsertParam.Field(Content.Field.CONTENT_VECTOR, vectors));
        // 3.执行操作
        milvusService.insert(Content.COLLECTION_NAME, fields);
    }

    @Test
    void searchTest(){
        // 0.加载向量集合
        milvusService.loadCollection(Content.COLLECTION_NAME);
        // 1.对问题进行嵌入
        EmbeddingResponse embeddings = chatClient.embeddings("台风“苏拉”什么时候，在哪儿登录？");
        List<Float> vectors = embeddings.getData().get(0).getEmbedding();
        SearchParamVo searchParamVo = SearchParamVo.builder()
                .collectionName(Content.COLLECTION_NAME)
                .queryVectors(Collections.singletonList(vectors))
                .topK(3)
                .build();
        // 2.在向量数据库中进行搜索内容知识
        List<List<SearchResultVo>> lists = milvusService.searchTopKSimilarity(searchParamVo);
        lists.forEach(searchResultVos -> {
            searchResultVos.forEach(searchResultVo -> {
                System.out.println(searchResultVo.getConent());
            });
        });
    }
    @Test
    void ChatBasedContentTest(){
        // 0.加载向量集合
        milvusService.loadCollection(Content.COLLECTION_NAME);
        // 1.对问题进行嵌入
        String question = "台风“苏拉”什么时候，在哪儿登录？";
        EmbeddingResponse embeddings = chatClient.embeddings(question);
        List<Float> vectors = embeddings.getData().get(0).getEmbedding();
        SearchParamVo searchParamVo = SearchParamVo.builder()
                .collectionName(Content.COLLECTION_NAME)
                .queryVectors(Collections.singletonList(vectors))
                .topK(3)
                .build();
        // 2.在向量数据库中进行搜索内容知识
        StringBuffer buffer = new StringBuffer();
        List<List<SearchResultVo>> lists = milvusService.searchTopKSimilarity(searchParamVo);
        lists.forEach(searchResultVos -> {
            searchResultVos.forEach(searchResultVo -> {
                buffer.append(searchResultVo.getConent());
            });
        });
        // 3.进行对话
        String prompt = "请你充分理解下面的内容，然后回答问题：";
        String content = buffer.toString();
        String result = chatClient.chatCompletion(prompt + content + question);
        System.out.println(result);
    }

    @Test
    void loadData() throws IOException {
        // Read the dataset file
        Path file = Path.of("src/main/resources/data/medium_articles_2020_dpr.json");
        String content = Files.readString(file);
        // Load dataset
        JSONObject dataset = JSON.parseObject(content);
        List<JSONObject> rows = getRows(dataset.getJSONArray("rows"), 2);
        System.out.println(rows);
    }
    public static List<JSONObject> getRows(JSONArray dataset, int counts) {
        List<JSONObject> rows = new ArrayList<>();
        for (int i = 0; i < counts; i++) {
            JSONObject row = dataset.getJSONObject(i);
            List<Float> vectors = row.getJSONArray("title_vector").toJavaList(Float.class);
            Long reading_time = row.getLong("reading_time");
            Long claps = row.getLong("claps");
            Long responses = row.getLong("responses");
            row.put("title_vector", vectors);
            row.put("reading_time", reading_time);
            row.put("claps", claps);
            row.put("responses", responses);
            row.remove("id");
            rows.add(row);
        }
        return rows;
    }

    @Test
    void getFileds() throws IOException {
        Path file = Path.of("src/main/resources/data/medium_articles_2020_dpr.json");
        String content = Files.readString(file);
        // Load dataset
        JSONObject dataset = JSON.parseObject(content);
        List<InsertParam.Field> field = getFields(dataset.getJSONArray("rows"), 1);
        System.out.println(field);
    }

    public static List<InsertParam.Field> getFields(JSONArray dataset, int counts) {
        List<InsertParam.Field> fields = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        List<List<Float>> title_vectors = new ArrayList<>();
        List<String> links = new ArrayList<>();
        List<Long> reading_times = new ArrayList<>();
        List<String> publications = new ArrayList<>();
        List<Long> claps_list = new ArrayList<>();
        List<Long> responses_list = new ArrayList<>();

        for (int i = 0; i < counts; i++) {
            JSONObject row = dataset.getJSONObject(i);
            titles.add(row.getString("title"));
            title_vectors.add(row.getJSONArray("title_vector").toJavaList(Float.class));
            links.add(row.getString("link"));
            reading_times.add(row.getLong("reading_time"));
            publications.add(row.getString("publication"));
            claps_list.add(row.getLong("claps"));
            responses_list.add(row.getLong("responses"));
        }

        fields.add(new InsertParam.Field("title", titles));
        fields.add(new InsertParam.Field("title_vector", title_vectors));
        fields.add(new InsertParam.Field("link", links));
        fields.add(new InsertParam.Field("reading_time", reading_times));
        fields.add(new InsertParam.Field("publication", publications));
        fields.add(new InsertParam.Field("claps", claps_list));
        fields.add(new InsertParam.Field("responses", responses_list));

        return fields;
    }


    @Test
    void searchTopKSimilarity() throws IOException {
        // Search data
        Path file = Path.of("src/main/resources/data/medium_articles_2020_dpr.json");
        String content = Files.readString(file);
        // Load dataset
        JSONObject dataset = JSON.parseObject(content);
        List<JSONObject> rows = getRows(dataset.getJSONArray("rows"), 10);
        // You should include the following in the main function

        List<List<Float>> queryVectors = new ArrayList<>();
        List<Float> queryVector = rows.get(0).getJSONArray("title_vector").toJavaList(Float.class);
        queryVectors.add(queryVector);

        // Prepare the outputFields
        List<String> outputFields = new ArrayList<>();
        outputFields.add("title");
        outputFields.add("link");

        // Search vectors in a collection
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName("medium_articles")
                .withVectorFieldName("title_vector")
                .withVectors(queryVectors)
                .withExpr("claps > 30 and reading_time < 10")
                .withTopK(3)
                .withMetricType(MetricType.L2)
                .withParams("{\"nprobe\":10,\"offset\":2, \"limit\":3}")
                .withConsistencyLevel(ConsistencyLevelEnum.BOUNDED)
                .withOutFields(outputFields)
                .build();

        R<SearchResults> response = milvusClient.search(searchParam);

        SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
        System.out.println("Search results");
        for (int i = 0; i < queryVectors.size(); ++i) {
            List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(i);
            List<QueryResultsWrapper.RowRecord> rowRecords = wrapper.getRowRecords();
            for (int j = 0; j < scores.size(); ++j) {
                SearchResultsWrapper.IDScore score = scores.get(j);
                QueryResultsWrapper.RowRecord rowRecord = rowRecords.get(j);
                System.out.println("Top " + j + " ID:" + score.getLongID() + " Distance:" + score.getScore());
                System.out.println("Title: " + rowRecord.get("title"));
                System.out.println("Link: " + rowRecord.get("link"));
            }
        }
            // Output
            // Search results
            // Top 0 ID:442206870369024034 Distance:0.41629803
            // Title: Coronavirus shows what ethical Amazon could look like
            // Top 1 ID:442206870369021531 Distance:0.4360938
            // Title: Mortality Rate As an Indicator of an Epidemic Outbreak
            // Top 2 ID:442206870369024868 Distance:0.48886314
            // Title: How Can AI Help Fight Coronavirus?
    }
}