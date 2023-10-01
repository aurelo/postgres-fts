connecting to docker pg instance:
```
docker exec -it postgres-fts-db-1 bash
```

connect to instance with psql
```
psql fts user
```

list test search configuration

```
\dF
```


share dir of pg configuration

```
pg_config --sharedir
```

stop words

```
/usr/share/postgresql/15/tsearch_data
```

-----


[basic showcase](/DOCS/PGshowcase.MD)

[building fts document](/DOCS/materialized_view.MD)

[misspellings](/DOCS/misspelling.MD)