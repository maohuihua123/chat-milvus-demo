package com.mao.milvus.demo.parse;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class TikaParse implements DocPrase {
    private static final int MAX_LENGTH = 250;

    public List<String> parse(InputStream inputStream) throws Exception {
        try (inputStream) {
            // Create a Tika parser
            Parser autoDetectParser = new AutoDetectParser();
            ParseContext context = new ParseContext();
            // Create a BodyContentHandler to store extracted text content
            BodyContentHandler handler = new BodyContentHandler(300000);
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
                if (currentLength + sentence.length() <= MAX_LENGTH) {
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
            paragraphs.forEach(System.out::println);
            // return list
            return paragraphs;
        }
    }

}
