For misspeling create unique words using ts_stat, materialized view and index

```
select word from  ts_stat(
   'select   to_tsvector(''simple'', fd.title)
    ||       to_tsvector(''simple'', fd.description)
    ||       to_tsvector(''simple'', fd.meta) vect
    from     fts_documents fd'
    )
    
    
create materialized view fts_words
as
select word from  ts_stat(
   'select   to_tsvector(''simple'', fd.title)
    ||       to_tsvector(''simple'', fd.description)
    ||       to_tsvector(''simple'', fd.meta) vect
    from     fts_documents fd'
    )
    
create extension pg_trgm

create index words_idx on fts_words using gin(word gin_trgm_ops)


create extension unaccent;

select   word
,        unaccent(word) 
,        similarity(v.word, 'somthing') 
from     fts_words v
where    word % 'somthing'
order by word <-> 'somthing'

```