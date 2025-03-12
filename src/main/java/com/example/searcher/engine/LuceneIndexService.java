package com.example.searcher.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.searcher.model.SearchObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class LuceneIndexService {

    private final Map<String, Directory> indexMap = new ConcurrentHashMap<>();
    private final StandardAnalyzer analyzer = new StandardAnalyzer();

    @PostConstruct
    public void init() throws IOException {
        // Load English phrases
        buildIndex("en", "phrases_en.json");
        // Load Russian phrases
        buildIndex("ru", "phrases_ru.json");
    }

    private void buildIndex(String lang, String fileName) throws IOException {
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        List<SearchObject> searchObjects = loadPhrases(fileName);
        for (SearchObject searchObject : searchObjects) {
            Document doc = new Document();
            doc.add(new TextField("triggerText", searchObject.getTriggerText(), Field.Store.YES));
            for (String keyword : searchObject.getKeywords()) {
                doc.add(new TextField("keyword", keyword, Field.Store.YES));
            }
            writer.addDocument(doc);
        }
        writer.close();
        indexMap.put(lang, index);
    }

    private List<SearchObject> loadPhrases(String fileName) throws IOException {
        InputStream inputStream = new ClassPathResource(fileName).getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(inputStream, new TypeReference<List<SearchObject>>() {
        });
    }

    public Directory getIndex(String lang) {
        return indexMap.get(lang);
    }

    public StandardAnalyzer getAnalyzer() {
        return analyzer;
    }
}
