package hr.kanezi.postgres.fts.quotes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuotesEntityRepository extends JpaRepository<QuotesEntity, Long> {

    @Query(value = "select q.* from quotes q order by  q.author||' '||q.quote <-> :q limit 10", nativeQuery = true)
    List<QuotesEntity> findSimilarQuotes(@Param("q") String q);

    @Query(value =
    """
    select   q.id
    ,        ts_headline(author, websearch_to_tsquery(:q), 'startSel=<em> stopSel=</em>') author
    ,        ts_headline(quote, websearch_to_tsquery(:q), 'startSel=<em> stopSel=</em>') quote
    ,        fts
    from     quotes q
    where    fts@@ websearch_to_tsquery(:q)  limit 10
    """, nativeQuery = true)
    List<QuotesEntity> ftsFindQuotes(@Param("q") String q);

}
