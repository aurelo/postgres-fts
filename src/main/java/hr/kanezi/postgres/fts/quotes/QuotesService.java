package hr.kanezi.postgres.fts.quotes;

import lombok.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Value
public class QuotesService {

    QuotesEntityRepository quotesEntityRepository;

    public List<QuotesEntity> getQuotes() {
        return quotesEntityRepository.findAll();
    }

    public List<QuotesEntity> findQuotes(String query) {
        return quotesEntityRepository.findSimilarQuotes(query);
    }

    public List<QuotesEntity> ftsFindQuotes(String query) {
        return quotesEntityRepository.ftsFindQuotes(query);
    }
}
