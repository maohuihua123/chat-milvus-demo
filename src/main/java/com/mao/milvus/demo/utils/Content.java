package com.mao.milvus.demo.utils;

public class Content {
    /**
     * 集合名称
     */
    public static final String COLLECTION_NAME = "content";
    /**
     * 分区名称
     */
    public static final String PARTITION_NAME = "content_partion";
    /**
     * 索引名称
     */
    public static final String CONTENT_INDEX = "content_index";
    /**
     * 分片数量
     */
    public static final Integer SHARDS_NUM = 1;
    /**
     * 分区数量
     */
    public static final Integer PARTITION_NUM = 1;

    /**
     * 分区前缀
     */
    public static final String PARTITION_PREFIX = "shards_";
    /**
     * 文本内容最大长度
     */
    public static final Integer MAX_LENGTH = 1024;

    /**
     * 向量值长度
     */
    public static final Integer FEATURE_DIM = 384;
    /**
     * 字段
     */
    public static class Field {
        /**
         * 主键id
         */
        public static final String ID = "id";
        /**
         * 文本内容
         */
        public static final String CONTENT = "content";
        /**
         * 向量值
         */
        public static final String CONTENT_VECTOR = "content_vector";
    }
}
