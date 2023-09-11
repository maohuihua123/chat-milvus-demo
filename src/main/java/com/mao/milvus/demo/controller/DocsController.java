package com.mao.milvus.demo.controller;

import com.mao.milvus.demo.client.ChatClient;
import com.mao.milvus.demo.client.embeddings.EmbeddingData;
import com.mao.milvus.demo.client.embeddings.EmbeddingResponse;
import com.mao.milvus.demo.parse.TikaParse;
import com.mao.milvus.demo.service.MilvusService;
import com.mao.milvus.demo.utils.Content;
import com.mao.milvus.demo.vo.ReplyMsg;
import io.milvus.param.dml.InsertParam;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class DocsController {
    @Resource
    private ChatClient chatClient;
    @Resource
    private MilvusService milvusService;
    @Resource
    private TikaParse docParse;

    @PostMapping("/upload")
    public ReplyMsg uploadTxt(MultipartFile file) {
        try {
            List<String> sentenceList = docParse.parse(file.getInputStream());
            return insertVector(sentenceList);
        } catch (Exception e) {
            return ReplyMsg.ofErrorMsg(e.getMessage());
        }
    }

    /**
     * 可以将一个文档创建一个Collection
     *
     * @param collectionName CollectionName
     * @return ReplyMsg
     */
    @GetMapping("/createCollection")
    public ReplyMsg createCollection(@RequestParam String collectionName){
        Boolean created = milvusService.creatCollection(collectionName);
        Boolean index = milvusService.createIndex(collectionName);
        if (created && index){
            return ReplyMsg.ofErrorMsg("success");
        }
        return ReplyMsg.ofErrorMsg("error");
    }

    private ReplyMsg insertVector(List<String> sentenceList){
        // 1.获得向量
        EmbeddingResponse embeddings = chatClient.embeddings(sentenceList);
        List<List<Float>> vectors = embeddings.getData().stream().map(EmbeddingData::getEmbedding).toList();
        // 2.准备插入向量数据库
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(Content.Field.CONTENT, sentenceList));
        fields.add(new InsertParam.Field(Content.Field.CONTENT_VECTOR, vectors));
        // 3.执行操作
        return milvusService.insert(Content.COLLECTION_NAME, fields);
    }
}
