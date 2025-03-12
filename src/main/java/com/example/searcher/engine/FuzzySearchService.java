package com.example.searcher.engine;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class FuzzySearchService implements SearchService {

    public static final int MAX_EDITS = 2; // 2 is the maximum edit distance
    @Autowired
    private LuceneIndexService luceneIndexService;

    public String[] search(String queryString, String lang) throws Exception {
        if (queryString == null || queryString.length() < 3) {
            throw new IllegalArgumentException("Query must be at least 3 characters long.");
        }

        Directory index = luceneIndexService.getIndex(lang);
        if (index == null) {
            throw new IllegalArgumentException("Unsupported language: " + lang);
        }

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Create a FuzzyQuery for approximate matching
        Query query = new FuzzyQuery(new Term("keyword", queryString), MAX_EDITS); // 2 is the maximum edit distance

        TopDocs topDocs = searcher.search(query, 3);
        String[] results = new String[topDocs.scoreDocs.length];
        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = topDocs.scoreDocs[i];
            Document doc = searcher.doc(scoreDoc.doc);
            results[i] = doc.get("triggerText");
        }
        reader.close();
        return results;
    }
}
