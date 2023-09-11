package com.mao.milvus.demo;


import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ContentParserTest {

    @Test
    void TxtTest() throws Exception {
        // Specify the document file
        File pdfFile = new File("C:\\Users\\mhh\\Desktop\\file\\测试文档.txt");

        try (InputStream inputStream = new FileInputStream(pdfFile)) {
            // Create a Tika parser
            Parser autoDetectParser = new AutoDetectParser();
            ParseContext context = new ParseContext();
            // Create a BodyContentHandler to store extracted text content
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            // Use Tika to parse the document
            autoDetectParser.parse(inputStream, handler, metadata, context);
            // Get the entire document content as a single string
            String text = handler.toString();
            String replaceAll = text.replaceAll("\\s+", "");
            // Divide the text into sentences
            String[] sentences = replaceAll.split("[.。!！?？]+");
            // Process and extracted paragraph
            List<String> paragraphs = new ArrayList<>();
            // Collect the currentParagraph
            int currentLength = 0;
            StringBuilder currentParagraph = new StringBuilder();
            for (String sentence : sentences) {
                if (currentLength + sentence.length() <= 400) {
                    currentParagraph.append(sentence).append(".");
                    currentLength += (sentence.length() + 1);
                } else {
                    paragraphs.add(currentParagraph.toString().strip());
                    currentParagraph = new StringBuilder(sentence + ".");
                    currentLength = (sentence.length() + 1);;
                }
            }
            // Add the last paragraph
            paragraphs.add(currentParagraph.toString().strip());
            // return list
            paragraphs.forEach(string -> {
                System.out.println(string);
                System.out.println();
            });
        }
    }

}
