package hr.kanezi.postgres.fts.quotes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quotes")
@Data
@NoArgsConstructor
public class QuotesEntity {

    @Id
    Long id;

    String quote;
    String author;
    String fts;
}
