(ns elasticsearch-tools.elasticsearch-reindex
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [taoensso.timbre :as timbre]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [elasticsearch-tools.common :as c])
  (:gen-class))

;; TODO: Add ES Request Error Checking and/or Possible Timeout.

(def ^:dynamic *url*)
(def ^:dynamic *old-index*)
(def ^:dynamic *new-index*)
(def ^:dynamic *alias*)

(def ^:private scroll-time
  "10m")

(def ^:private size
  1000)

(defn- old-index-url
  []
  (format "%s/%s"
          *url* *old-index*))

(defn- old-index-scroll-url
  []
  (format "%s/_search?search_type=scan&scroll=%s"
          (old-index-url) scroll-time))

(defn- scroll-url
  []
  (memoize #(format "%s/_search/scroll?scroll=%s" *url* scroll-time)))

(defn- alias-url
  []
  (format "%s/_aliases" *url*))

(defn- bulk-url
  []
  (memoize #(format "%s/_bulk" *url*)))

(defn- set-version
  [action]
  (-> (assoc-in action [:index :_version] (c/now))
     (assoc-in [:index :_version_type] "external")))

(defn create-bulk-request
  "Create the elasticsearch bulk request for updating the new index."
  [hits]
  (-> (reduce (fn [acc hit]
               (let [action (-> (select-keys hit [:_index :_type :_id])
                               (assoc-in [:_index] *new-index*)
                               (->> (hash-map :index))
                               (set-version))]
                 (-> action
                    (vector (:_source hit))
                    (->>
                     (map json/generate-string))
                    (interleave (repeat "\n"))
                    (string/join)
                    (->>
                     (conj acc)))))
             [] hits)
     (string/join)))

(defn- create-atomic-alias-actions
  "Create the request body for the atomic alias change."
  []
  (let [m {:actions
           [{:remove {:alias *alias*
                      :index *old-index*}}
            {:add {:alias *alias*
                   :index *new-index*}}]}]
    (json/generate-string m)))

(defn- scan-search
  "Scan-search to get initial scroll-id."
  []
  (timbre/info "Get initial scroll-id")
  (let [req-body {:query {:match_all {}} :size size}]
    (let [response (http/get (old-index-scroll-url)
                             {:body (json/generate-string req-body)
                              :content-type :json})]
      (c/log-resp "Scan-Search Response" response)
      response)))

(defn- scroll-search
  "Scroll-search to batch through documents."
  [scroll-id]
  (let [req-params {:query-params {:scroll_id scroll-id}}]
    (let [response (http/get ((scroll-url)) req-params)]
      (c/log-resp "Scroll-Search Response" response)
      response)))

(defn- fetch-hits-and-bulk-update
  [scroll-id total-hits]
  (timbre/info "scroll-id:" scroll-id)
  (let [response (scroll-search scroll-id)
        resp-body (json/parse-string (:body response) true)
        hits (get-in resp-body [:hits :hits])
        num-hits (count hits)
        scroll-id* (:_scroll_id resp-body)]
    (timbre/info "number of hits:" num-hits)
    (when (seq hits)
      (timbre/info "total-hits - number of hits:" (- total-hits num-hits))
      (let [bulk-response (http/post ((bulk-url))
                                     {:body (create-bulk-request hits)
                                      :content-type :json})]
        (c/log-resp "Bulk Response" bulk-response))
      (recur scroll-id* total-hits))))

(defn- delete-old-index
  []
  (let [delete-resp (http/delete (old-index-url))]
    (c/log-resp "Delete Old Index" delete-resp)))

(defn reindex
  []
  (let [response (scan-search)
        resp-body (json/parse-string (:body response) true)
        init-scroll-id (:_scroll_id resp-body)
        total-hits (get-in resp-body [:hits :total])]
    (fetch-hits-and-bulk-update init-scroll-id total-hits)
    (let [alias-response (http/post (alias-url)
                                    {:body (create-atomic-alias-actions)
                                     :content-type :json})]
      (c/log-resp "Alias Atomic Update" alias-response)
      (delete-old-index))))

(def cli-options
  [["-h" "--help" "Print this help"
    :default false
    :flag true]
   ["-d" "--url URL" "ElasticSearch Url"
    :id :url
    :default "http://localhost:9200"]
   ["-o" "--old-index OLD-INDEX" "Old Index (from)"
    :id :old-index
    :default "old_index"]
   ["-n" "--new-index NEW-INDEX" "New Index (to)"
    :id :new-index
    :default "new_index"]
   ["-a" "--alias ALIAS" "Alias"
    :id :alias
    :default "alias"]])

(defn -main
  [& args]
  (let [{:keys [options errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (c/exit 0 summary)
      errors (c/exit 1 errors)
      :else (binding
                [*url* (:url options)
                 *old-index* (:old-index options)
                 *new-index* (:new-index options)
                 *alias* (:alias options)]
      (reindex)))))
