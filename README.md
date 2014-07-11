# elasticsearch-tools

[![Build Status](https://travis-ci.org/sfx/elasticsearch-tools.svg)](https://travis-ci.org/sfx/elasticsearch-tools)

*Current Tools*:

- Reindexing
- Drop (Full-Delete of Indexes, Types, etc...)

## Reindexing

What is reindexing?

From the Elasticsearch docs ...

> While you can add new types to an index, or add new fields to a type, you can’t add new analyzers or make changes to
existing fields. If you were to do so, the data that has already been indexed would be incorrect and your searches would
no longer work as expected.

> The simplest way to apply these changes to your existing data is just to reindex: create a new index with the new
settings and copy all of your documents from the old index to the new index.

###Options

Here are the arguments the script takes:

- `-h` or `--help` to get the possible options
- `-d` or `--url` to set the ElasticSearch Url
- `-o` or `--old-index` to set the indexing your reindexing from
- `-n` or `--new-index` to set the indexing your reindexing to
- `-a` or `--alias` to set the alias

There are defaults for development purposes, but **please provide these values**.

## Drop

What does drop mean in this context?

It simply means ...

- removing all indexes (and associated types)
- clearing the cache against *_all*
- refreshing *_all*

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
        - `$ java -jar uberjar-standalone-reindex`
        - `$ java -jar uberjar-standalone-drop`

- Via *lein* using an **alias** to a profile. All aliases can be found in `project.clj`.
    - `$ lein reindex -- -d "http://elasticsearch.com:9200" -o "old_index" -n "new_index" -a "elasticsearch_alias"`
    - `$ lein drop -- -d "http://elasticsearch.com:9200"`

## Reference
- [Elasticsearch - Reindexing Your Data](http://www.elasticsearch.org/guide/en/elasticsearch/guide/current/reindex.html)

- [Elasticsearch - Reindexing with Zero Downtime](http://www.elasticsearch.org/blog/changing-mapping-with-zero-downtime/)

- [Elasticsearch - Delete Index](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/indices-delete-index.html)

## License

Copyright © 2014 Brian Bowman, Zeeshan Lakhani, SFX Entertainment

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
