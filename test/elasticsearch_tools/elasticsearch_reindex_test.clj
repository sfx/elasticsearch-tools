(ns elasticsearch-tools.elasticsearch-reindex-test
  (:require [elasticsearch-tools.elasticsearch-reindex :as er]
            [elasticsearch-tools.common :refer [now]]
            [clojure.string :as string]
            [clojure.test :refer :all]))

(def es-hits-stub
  [{:_index "twitter",
    :_type  "tweet",
    :_id    "1",
    :_source {"user"     "kimchy",
              "postDate" "2009-11-15T14:12:12",
              "message"  "trying out Elasticsearch"}}
   {:_index "twitter",
    :_type  "tweet",
    :_id    "2",
    :_source {"user"     "kimchy2",
              "postDate" "2009-11-15T16:12:12",
              "message"  "trying out Elasticsearch 2"}}])

(defn bulk-request-test
  []
  (binding [er/*new-index* "test"]
    (let [req (er/create-bulk-request es-hits-stub)]
      (testing "Check total lines split by new lines"
        (is (= (count (string/split-lines req)) 4))))))
