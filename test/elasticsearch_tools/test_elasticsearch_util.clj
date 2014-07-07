(ns elasticsearch-tools.test-elasticsearch-util
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [elasticsearch-tools.elasticsearch-reindex :as er]
            [elasticsearch-tools.elasticsearch-drop :as ed]))

(def es-url "http://localhost:9200")
(def first-index "test_v1")
(def es-alias "test")
(def es-type "tweet")

(def es-init-stub
  [{:_index first-index
    :_type  es-type
    :_id    "1"
    :_source {"user"     "kimchy"
              "postDate" "2009-11-15T14:12:12"
              "message"  "trying out Elasticsearch"}}
   {:_index first-index
    :_type  es-type
    :_id    "2"
    :_source {"user"     "kimchy2"
              "postDate" "2009-11-15T16:12:12"
              "message"  "trying out Elasticsearch 2"}}])

(def es-alias-init
  {:actions [{:add {:index first-index :alias es-alias}}]})

(defn bulk_populate
  [url]
  (let [bulk-url (format "%s/_bulk" url)
        alias-url (format "%s/_aliases" url)]
    ;; Using create-bulk-request here to create the initial (new) index
    (http/post bulk-url {:body (er/create-bulk-request es-init-stub)
                         :content-type :json})
    (http/post alias-url {:body (json/generate-string es-alias-init)
                          :content-type :json})))

(defn init-es
  [f]
  (binding [er/*url* es-url
            er/*new-index* first-index
            er/*alias* es-alias]
    (bulk_populate er/*url*)
    (binding [ed/*url* es-url]
      (ed/clean-up))
    (f)
    (binding [ed/*url* es-url]
      (ed/es-drop))))
