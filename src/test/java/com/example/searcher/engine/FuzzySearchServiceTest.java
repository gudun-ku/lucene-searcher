package com.example.searcher.engine;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class FuzzySearchServiceTest {

    @Mock
    private LuceneIndexService luceneIndexService;

    @InjectMocks
    private FuzzySearchService fuzzySearchService;

    private Directory englishIndex;
    private Directory russianIndex;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Create in-memory Lucene indexes for testing
        englishIndex = createTestIndex("en", new String[][]{
                {"DevOps practices improve software delivery", "devops", "practices", "software", "delivery"},
                {"Docker simplifies containerization", "docker", "containerization"}
        });

        russianIndex = createTestIndex("ru", new String[][]{
                {"DevOps практики улучшают доставку программного обеспечения", "devops", "практики", "доставка", "по"},
                {"Docker упрощает контейнеризацию", "docker", "контейнеризация"}
        });

        // Mock LuceneIndexService to return the test indexes
        Map<String, Directory> indexMap = new HashMap<>();
        indexMap.put("en", englishIndex);
        indexMap.put("ru", russianIndex);
        lenient().when(luceneIndexService.getIndex(anyString())).thenAnswer(invocation -> {
            String lang = invocation.getArgument(0);
            return indexMap.get(lang);
        });
    }

    private Directory createTestIndex(String lang, String[][] phrases) throws Exception {
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(index, config);

        for (String[] phraseData : phrases) {
            Document doc = new Document();
            doc.add(new TextField("phrase", phraseData[0], Field.Store.YES));
            for (int i = 1; i < phraseData.length; i++) {
                doc.add(new TextField("keyword", phraseData[i], Field.Store.YES));
            }
            writer.addDocument(doc);
        }
        writer.close();
        return index;
    }

    @Test
    void testSearch_English() throws Exception {
        String[] results = fuzzySearchService.search("delivry", "en");
        assertArrayEquals(new String[]{"DevOps practices improve software delivery"}, results);
    }

    @Test
    void testSearch_Russian() throws Exception {
        String[] results = fuzzySearchService.search("доставка", "ru");
        assertArrayEquals(new String[]{"DevOps практики улучшают доставку программного обеспечения"}, results);
    }

    @Test
    void testSearch_PartialMatch() throws Exception {
        String[] results = fuzzySearchService.search("dock", "en");
        assertArrayEquals(new String[]{"Docker simplifies containerization"}, results);
    }

    @Test
    void testSearch_EmptyQuery() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> fuzzySearchService.search("", "en"));
        assertEquals("Query must be at least 3 characters long.", exception.getMessage());
    }

    @Test
    void testSearch_ShortQuery() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> fuzzySearchService.search("ab", "en")
        );
        assertEquals("Query must be at least 3 characters long.", exception.getMessage());
    }

    @Test
    void testSearch_UnsupportedLanguage() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> fuzzySearchService.search("test", "fr"));
        assertEquals("Unsupported language: fr", exception.getMessage());
    }

    @Test
    void testSearch_NoResults() throws Exception {
        String[] results = fuzzySearchService.search("nonexistent", "en");
        assertEquals(0, results.length);
    }
}