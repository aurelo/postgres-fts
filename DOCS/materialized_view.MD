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


triggers:

```
  create or replace function refresh_fts_documents()
  returns trigger 
  language plpgsql
  as $$
  begin
      refresh materialized view concurrently fts_documents;
      return null;
  end $$;
```

```
create trigger refresh_fts_4_quotes
  after insert or update or delete or truncate
  on quotes
  for each statement
  execute procedure refresh_fts_documents();
```

to refresh materialized view concurrently table needs unique index

```
create unique index idx_doc_id on fts_documents (id)
```

Example of inserts on quotes:

```
insert into quotes (id, quote, author)
  values (10000, 'There are only two hard things in Computer Science: cache invalidation and naming things.', 'Phil Karlton')

    select   *
    from     quotes q 
    order by id desc 
    
    select   *
    from     fts_documents 
    where    id = 'QUOTES_10000'

```