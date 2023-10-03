For synonyms rules file needs to be placed on pg shared directory

There's already example xsyn_sample.rules there with synonyms for english lang for **supernova**

List of text search dictionaries:

```\dFd```

list of text search configurations:

```\dF```

```
create extension dict_xsyn;

alter text search dictionary xsyn(rules='xsyn_sample');
-- cat /usr/share/postgresql/15/tsearch_data/xsyn_sample.rules
-- has one example synonym: "supernova sn sne 1987a"


alter text search configuration english
    alter mapping for word, asciiword with xsyn, english_stem;


SELECT ts_debug('english', 'supernova')
,      to_tsvector('english', 'supernova')
,      ts_rank(to_tsvector('english', 'supernova'), 'sn'::tsquery)
```