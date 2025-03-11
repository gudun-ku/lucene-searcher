package com.example.searcher.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.example.searcher.model.Phrase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private Directory index;
    private StandardAnalyzer analyzer;

    public LuceneIndexService() throws IOException {
        this.index = new ByteBuffersDirectory();
        this.analyzer = new StandardAnalyzer();
        buildIndex();
    }

    private void buildIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);

        List<Phrase> phrases = loadPhrases();
        for (Phrase phrase : phrases) {
            Document doc = new Document();
            doc.add(new TextField("phrase", phrase.getPhrase(), Field.Store.YES));
            for (String keyword : phrase.getKeywords()) {
                doc.add(new TextField("keyword", keyword, Field.Store.YES));
            }
            writer.addDocument(doc);
        }
        writer.close();
    }

    private List<Phrase> loadPhrases() throws IOException {
        InputStream inputStream = new ClassPathResource("phrases.json").getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(inputStream, new TypeReference<List<Phrase>>() {
        });
    }

    public Directory getIndex() {
        return index;
    }

    public StandardAnalyzer getAnalyzer() {
        return analyzer;
    }
}
