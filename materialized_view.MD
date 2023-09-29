```
create materialized view fts_documents
as
select   quotes.*
,        setweight(to_tsvector('simple', quotes.title), 'A') 
||       setweight(to_tsvector(quotes.description), 'A')
||       setweight(to_tsvector(quotes.meta), 'B') fts
from    (
    select   'QUOTES'||'_'||q.id id 
    ,        'QUOTES' type
    ,         q.author title
    ,         q.quote description
    ,         (
      select   coalesce(string_agg(t.tag, ', '), '') AS tags
      from     tags t
      join     quote_tags qt on t.id = qt.tag_id
      where    qt.quote_id = q.id
    ) meta
    ,         '/quotes/'||q.id url
    from      quotes q
) quotes
union all
select   movies.*
,        setweight(to_tsvector('simple', movies.title), 'A') 
||       setweight(to_tsvector('simple', movies.description), 'A')
||       setweight(to_tsvector(movies.meta), 'B') fts
from    (
select   'MOVIES'||'_'||m.id id
,        'MOVIES' type
,        m.title
,        coalesce(( 
           select   string_agg(a.name::text, ', '::text) as string_agg
           from     actors a
           join     movies_actors ma on ma.actor_id = a.id and ma.movie_id = m.id
           ), ''::text) as description
,        coalesce(( 
           select   string_agg(g.name::text, ', '::text) as string_agg
           from     genres g
           join     movies_genres mg on mg.genre_id = g.id and mg.movie_id = m.id
           where    mg.score > 0), ''::text) as meta
,         '/movies/'||m.id url
from     movies m
) movies


create index idx_doc_fts_search on fts_documents using  gin(fts)

```