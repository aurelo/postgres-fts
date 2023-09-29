package hr.kanezi.postgres.fts.search;

import lombok.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Value
public class FtsService {

    FtsDocumentRepository ftsDocumentRepository;

    List<FtsDocuments> search(String query){
        return Collections.emptyList();
    }
}
