(defproject elasticsearch-tools "0.1.0"
  :description "Tools for Dealing with ElasticSearch."
  :url "https://github.com/sfx/elasticsearch-tools"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [cheshire "5.3.1"]
                 [clj-http "0.9.1"]
                 [com.taoensso/timbre "3.1.6"]
                 [org.clojure/tools.cli "0.3.1"]]
  :target-path "target/%s"
  :aliases {"reindex" ["with-profile" "reindex" "run"]
            "drop" ["with-profile" "drop" "run"]}
  :aot [elasticsearch-tools.elasticsearch-reindex
        elasticsearch-tools.elasticsearch-drop]
  :profiles {:reindex {:main elasticsearch-tools.elasticsearch-reindex
                       :uberjar-name "uberjar-standalone-reindex"}
             :drop {:main elasticsearch-tools.elasticsearch-drop
                    :uberjar-name "uberjar-standalone-drop"}})
