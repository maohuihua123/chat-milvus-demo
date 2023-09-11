package com.mao.milvus.demo.parse;

import java.io.InputStream;
import java.util.List;

public interface DocPrase {
    List<String> parse(InputStream inputStream) throws Exception;
}
