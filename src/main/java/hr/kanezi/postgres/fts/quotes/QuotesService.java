package hr.kanezi.postgres.fts.quotes;

import lombok.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Value
public class QuotesService {

    QuotesEntityRepository quotesEntityRepository;

    public List<QuotesEntity> getQuotes() {
        return quotesEntityRepository.findAll();
    }

    public Optional<QuotesEntity> findById(Long quoteId) {
        return quotesEntityRepository.findById(quoteId);
    }

    public List<QuotesEntity> findQuotes(String query) {
        return quotesEntityRepository.findSimilarQuotes(query);
    }

    public List<QuotesEntity> ftsFindQuotes(String query) {
        return quotesEntityRepository.ftsFindQuotes(query);
    }

    public List<QuotesEntity> ftsFindQuotes2(String query) {
        return quotesEntityRepository.ftsFindQuotes2(query);
    }


}
