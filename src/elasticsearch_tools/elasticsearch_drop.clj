(ns elasticsearch-tools.elasticsearch-drop
    (:require [clj-http.client :as http]
              [clojure.tools.cli :refer [parse-opts]]
              [cheshire.core :as json]
              [taoensso.timbre :as timbre]
              [elasticsearch-tools.common :as c])
    (:gen-class))

(def ^:dynamic *url*)

(defn- all-url
  []
  (format "%s/_all" *url*))

(defn- cache-clear-url
  []
  (format "%s/_cache/clear" *url*))

(defn- refresh-url
  []
  (format "%s/_refresh" *url*))

(defn clean-up
  []
  (let [cache-response (http/post (cache-clear-url))
        refresh-response (http/post (refresh-url))]
    (c/log-resp "Cache Cleared" cache-response)
    (c/log-resp "Refreshed" refresh-response)))

(defn es-drop
  "Drop ElasticSearch Indexes and Types."
  []
  (let [response-delete (http/delete (all-url))
        resp-body (json/parse-string (:body response-delete) true)
        ack? (:acknowledged resp-body)]
    (if ack?
      (do (c/log-resp "Indexes and Types Dropped"
                      response-delete)
          (clean-up))
      (c/log-resp "Did Not Delete ElasticSearch Configuration"
                  response-delete))))

(def cli-options
  [["-h" "--help" "Print this help"
    :default false
    :flag true]
   ["-d" "--url URL" "ElasticSearch Url"
    :id :url
    :default "http://localhost:9200"]])

(defn -main
  [& args]
  (let [{:keys [options errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (c/exit 0 summary)
      errors (c/exit 1 errors)
      :else (binding
                [*url* (:url options)]
      (es-drop)))))
