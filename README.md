# 基于知识库的对话助手

>简单实现

## 涉及技术

* 文心千帆LLM：[https://console.bce.baidu.com/qianfan/overview](https://console.bce.baidu.com/qianfan/overview)
* 向量数据库Milvus：[https://github.com/milvus-io/milvus](https://github.com/milvus-io/milvus)
* 文档读取apache-tika：[https://github.com/apache/tika](https://github.com/apache/tika)

## 问答流程

![.\images\PDF-LangChain.jpg](https://github.com/maohuihua123/chat-milvus-demo/blob/main/images/PDF-LangChain.jpg)

**[流程图来源]：**[https://github.com/alejandro-ao/ask-multiple-pdfs](https://github.com/alejandro-ao/ask-multiple-pdfs)

## 代码实现

```java
@Test
void ChatBasedContentTest(){
    // 0.加载向量集合
    milvusService.loadCollection(ContentConst.COLLECTION_NAME);
    // 1.对问题进行嵌入
    String question = "台风“苏拉”什么时候，在哪儿登录？";
    EmbeddingResponse embeddings = chatClient.embeddings(question);
    List<Float> vectors = embeddings.getData().get(0).getEmbedding();
    SearchParamVo searchParamVo = SearchParamVo.builder()
            .collectionName(ContentConst.COLLECTION_NAME)
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

```

## 问答案例

1.问题

```
String question = "台风“苏拉”什么时候，在哪儿登录？";
```

2. 加载的知识库内容

```
------search TopK Similarity------

Top 0 ID:443798731579961321 Distance:0.8073174
Content: 记者9月2日从广东湛江海事局了解到，鉴于台风“苏拉”较大可能于3日早晨前后以热带风暴级别再次登陆湛江，目前，湛江已启动防热带气旋Ⅱ级应急响应。

Top 1 ID:443798731579961323 Distance:0.91673326
Content: 9月2日14时至3日14时，受“苏拉”影响，南海西北部、北部湾东部、琼州海峡及广东中西部沿海、广西南部沿海、海南北部沿海、珠江口以及香港和澳门等地将有6-8级、阵风9-10级的大风，“苏拉”中心经过的附近海面或地区风力有11-12级、阵风13-14级；受“海葵”影响，台湾海峡、巴士海峡、浙江南部沿海、福建沿海、广东东部沿海、台湾岛沿海将有6-8级，阵风9-10级大风，台湾以东洋面部分海域风力可达9-12级，阵风13-14级，“海葵”中心经过的附近海面风力可达13-16级，阵风17级及以上。中央气象台9月2日10时继续发布台风红色预警。

Top 2 ID:443798731579961322 Distance:1.0886467
 Content: 为应对水上突发事件，海事部门提前部署海上救援力量，根据台风动态更新社会应急力量布点，在琼州海峡北岸、湛江港霞山港区、海湾大桥、宝钢湛江钢铁基地对开水域、雷州半岛流沙湾等重要部位，部署大马力拖轮和海上专业救助船共9艘，确保能第一时间投入抢险救援。
 
Successfully!
```

3.进行对话问答

```java
String question = "台风“苏拉”什么时候，在哪儿登录？";
String prompt = "请你充分理解下面的内容，然后回答问题：";
String content = buffer.toString();
String result = chatClient.chatCompletion(prompt + content + question);
```

4.问答结果

```
根据原文信息得出，台风苏拉登录时间可能是3日早晨前后（文中提到，“鉴于台风“苏拉”较大可能于3日早晨前后以热带风暴级别再次登陆湛江”）
根据原文信息得出，地点是广东中西部沿海、广西南部沿海、海南北部沿海珠江口以及香港和澳门等地。（文中提到南海西北部、北部湾东部、琼州海峡及广东中西部沿海、广西南部沿海、海南北部沿海、珠江口以及香港和澳门）
```

## 效果展示
![image-20230912020354066](https://github.com/maohuihua123/chat-milvus-demo/blob/main/images/image-20230912020354066.png)



