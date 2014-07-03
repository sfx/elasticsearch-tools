# elasticsearch-tools

Current Tools:

- Re-Indexing
- Drop (Full-Delete of Indexes, Types, etc...)

## Re-Indexing

###Options

Here are the arguments the script takes:

- `-h` or `--help` to get the possible options
- `-d` or `--url` to set the ElasticSearch Url
- `-o` or `--old-index` to set the indexing your re-indexing from
- `-n` or `--new-index` to set the indexing your re-indexing to
- `-a` or `--alias` to set the alias

There are defaults for development purposes, but **please provide these values**.

## Drop

###Options

Here are the arguments the script takes:

- `-h` or `--help` to get the possible options
- `-d` or `--url` to set the ElasticSearch Url

## Usage

There are **two ways** to run these scripts.

- Via an Uberjar

    - Create Uberjars for each of the profiles, e.g. `lein with-profile reindex:drop uberjar`
    - The Uberjar for each profile will be named uberjar-standalone-<*profile-name*>, where profile-name
    would be something like **drop** or **reindex**, and located within the *target* folder, usually
    under a directory like <*profile-name*>+uberjar.
        - `java -jar uberjar-standalone-reindex`
        - `java -jar uberjar-standalone-drop`

- Via *lein* using an **alias** to a profile. All aliases can be found in `project.clj`. 
    - `$ lein reindex -- -d "http://elasticsearch.com:9200" -o "old_index" -n "new_index" -a "elasticsearch_alias"`
    - `leon drop -- -d "http://elasitcsearch.com:9200"`

## Reference

- [ElasticSearch Re-Indexing with Zero Downtime](http://www.elasticsearch.org/blog/changing-mapping-with-zero-downtime/)

- [ElasticSearch Delete Index](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/indices-delete-index.html)

## License

Copyright © 2014 Brian Bowman, Zeeshan Lakhani, SFX Entertainment

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.