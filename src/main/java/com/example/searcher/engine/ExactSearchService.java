package com.example.searcher.engine;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExactSearchService implements SearchService {

    @Autowired
    private LuceneIndexService luceneIndexService;

    @Override
    public String[] search(String queryString, String lang) throws Exception {
        Directory index = luceneIndexService.getIndex(lang);
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser("phrase", luceneIndexService.getAnalyzer());
        Query query = parser.parse(queryString);

        TopDocs topDocs = searcher.search(query, 3);
        String[] results = new String[topDocs.scoreDocs.length];
        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = topDocs.scoreDocs[i];
            Document doc = searcher.doc(scoreDoc.doc);
            results[i] = doc.get("phrase");
        }
        reader.close();
        return results;
    }
}
