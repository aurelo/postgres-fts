package hr.kanezi.postgres.fts.search;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FtsDocumentRepository extends JpaRepository<FtsDocuments, String> {
}
