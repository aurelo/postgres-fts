package hr.kanezi.postgres.fts.search;

import lombok.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Value
public class FtsService {

    FtsDocumentRepository ftsDocumentRepository;

    FtsWordsViewRepository ftsWordsViewRepository;

    public List<FtsDocuments> search(String query, Long limit){
        return ftsDocumentRepository.ftsSearch(query, limit);
    }

    public List<FtsWordView> misspellings(String query) {
        return ftsWordsViewRepository.findSimilar(query);
    }
}
