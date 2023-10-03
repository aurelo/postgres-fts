```
---------------------------------------------------
-- REGULAR TEXT SEATCH IN POSTGRES
---------------------------------------------------
select   q.*
,        regexp_match(q.quote, '^Any') regexp_match
from     quotes q
where    1=1
-- like --
--and      q."quote" like 'Any%'
--and      q."quote" like '%on_.' -- matches one. / reasons.
--and      q.quote ~~ 'Any%' 
-- ilike --
--and      q."quote" ilike 'any%'
--and      q.quote ~~* 'any%'
-- regexp
--and      q.quote ~ '^Any' 
--and      q.quote ~* '^any'
and      q.quote ilike '%dark%'  

-----------------------------------------------------
-- NEW DATATYPES:
--  tsvector - document stripped down to it's 
--           - processed form
--  tsquery  - predicate that represent search query 
-----------------------------------------------------
create table drop_me (
  id                 serial
 ,padded_string      char(10)
 ,non_padded_string  varchar(10)
 ,larger_text        text -- or varchar without ()
 -- new data types
 ,fts_document       tsvector
 ,fts_query          tsquery
)


select   *
from     drop_me

drop table drop_me

-- example of tokenizers --
select   ts_parse('default', 'please have a good day') -- parse sentence to tokens - tokenid # token
,        ts_lexize('english_stem', 'happiness') -- dictionary function - accepts token on input and return array of tokens

-- St. Francis Of Assisi quote
select   q.quote 
,        q.author
,        string_agg(t.tag, ', ') tags
from     quotes q
join     quote_tags qt on qt.quote_id = q.id
join     tags t on qt.tag_id = t.id
where    q.id = 1112
group by q.quote 
,        q.author


-----------------------------------------------------
-- TSVECTOR showcase
-----------------------------------------------------
select   to_tsvector('All the darkness in the world cannot extinguish the light of a single candle.') quote
,        to_tsvector('english', 'All the darkness in the world cannot extinguish the light of a single candle.') quote_en
,        to_tsvector('simple', 'St. Francis Of Assisi') author
,        'St. Francis Of Assisi'::tsvector 
,        to_tsvector('german', 'was machts du gerade')
--,        'inspirational' tags

-- contcatenation
select   to_tsvector('All the darkness in the world') quote
,        to_tsvector('Without the dark') quote_en
---
,        to_tsvector('All the darkness in the world')||to_tsvector('Without the dark') concatenated 


-----------------------------------------------------
-- TSQUERY showcase
-- <-> followed by
-- <2> 2 tokens away distance
-----------------------------------------------------
select   to_tsquery('world')
,        to_tsquery('world & light')
,        plainto_tsquery('world light')
,        phraseto_tsquery('darkness in the world cannot extinguish the light')
,        websearch_to_tsquery('cook and bake -Captain') 

-----------------------------------------------------
-- matching TSVECTOR with TSQUERY
--  @@ operator
-----------------------------------------------------

select   t.doc
,        to_tsquery('darkness') qry
from
(
    select   to_tsvector(q.quote) 
    ||       to_tsvector('simple', q.author)
    ||       to_tsvector(coalesce(string_agg(t.tag, ', '), '')) doc
    from     quotes q
    join     quote_tags qt on qt.quote_id = q.id
    join     tags t on qt.tag_id = t.id
    where    q.id = 1112
    group by q.quote
    ,        q.author 
) t
where   1=1
--and     t.doc @@ 'dark'
--and     t.doc @@ 'darkness' -- does not work
and     t.doc @@ to_tsquery('darkness')
-- operator are switchable
and     to_tsquery('darkness') @@ t.doc  



select   doc
,        qry1
,        qry2
,        ts_rank(doc, qry1)
,        ts_rank(doc, qry2)
,        ts_rank_cd(doc, qry1)
,        ts_rank_cd(doc, qry2)
from     
(
    select   t.doc
    ,        to_tsquery('darkness') qry1
    ,        to_tsquery('dimness') qry2
    from
    (
        select   to_tsvector(q.quote) 
        ||       to_tsvector('simple', q.author)
        ||       to_tsvector(coalesce(string_agg(t.tag, ', '), '')) doc
        from     quotes q
        join     quote_tags qt on qt.quote_id = q.id
        join     tags t on qt.tag_id = t.id
        where    q.id = 1112
        group by q.quote
        ,        q.author 
    ) t
) t2


--explain
select   quote
,        ts_headline(quote, websearch_to_tsquery(:p_query)) mark_result_quote
,        ts_headline(author, websearch_to_tsquery(:p_query)) mark_result_author
,        ts_headline(tags, websearch_to_tsquery(:p_query), 'startSel=<em> stopSel=</em>') mark_result_tags
,        author
,        tags
,        ts_rank(doc, websearch_to_tsquery(:p_query)) rank
,        doc
,        websearch_to_tsquery(:p_query) qry
from (
    select   q.quote
    ,        q.author
    ,        coalesce(string_agg(t.tag, ', '), '') tags
    ,        setweight(to_tsvector(q.quote), 'A') 
    ||       setweight(to_tsvector('simple', q.author), 'B')
    ||       setweight(to_tsvector(coalesce(string_agg(t.tag, ', '), '')), 'D') doc
    from     quotes q
    join     quote_tags qt on qt.quote_id = q.id
    join     tags t on qt.tag_id = t.id
--    where    q.id = 1112
    group by q.quote
    ,        q.author 
) t
where    1=1
and      doc @@ websearch_to_tsquery(:p_query) 
--and      doc @@ :p_query 
order by rank desc limit 10

------------------------------------------------------------------------------
-- DOES NOT WORK without explicit language, because function is not immutable
------------------------------------------------------------------------------
alter table quotes 
add column fts tsvector
generated always as (
    setweight(to_tsvector('english', quote), 'A') 
    ||       setweight(to_tsvector('simple', author), 'B')
)
stored;


create index on quotes using gin(fts);

explain analyze
select   q.quote
,        q.author
,        q.fts
from     quotes q
join     quote_tags qt on qt.quote_id = q.id
join     tags t on qt.tag_id = t.id
where    1=1
--and (setweight(to_tsvector('english', quote), 'A') 
--    ||       setweight(to_tsvector('simple', author), 'B')
--    )
--    @@ to_tsquery('little')   
and q.fts @@ to_tsquery('little')    

-----------------------------------------------------
-- PG_TRG
--
-----------------------------------------------------
create extension pg_trgm;


select   t.*
,        similarity(t.doc, 'litel') sim
from 
(
    select   q.author
    ,        q.quote
    ,        q.author||' '||q.quote doc
    from     quotes q
    where    q.author = 'Frank Zappa'
) t
--where   to_tsvector(t.doc) @@ to_tsquery('lit')
order by t.doc <-> 'lit' 



create index words_idx on quotes using gin(quote gin_trgm_ops);



select    q.*
,         similarity(q.quote, 'l') similarity
from      quotes q
order by  q.quote <-> 'boo' limit 10


```