(ns elasticsearch-tools.elasticsearch-reindex-test
  (:require [elasticsearch-tools.elasticsearch-reindex :as er]
            [elasticsearch-tools.elasticsearch-drop :as ed]
            [elasticsearch-tools.test-elasticsearch-util :refer
             [init-es es-init-stub es-type]]
            [clojure.string :as string]
            [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.test :refer :all]))

(use-fixtures :each init-es)

(deftest bulk-request-test
  (let [req (er/create-bulk-request es-init-stub)]
    (testing "Check total lines split by new lines"
      (is (= (count (string/split-lines req)) 4)))))

(deftest reindex-test
  (binding [er/*old-index* "test_v1"
            er/*new-index* "test_v2"
            ed/*url* er/*url*]
    (er/reindex)
    (ed/clean-up)
    (let [old-status (-> (http/get (format "%s/%s/%s/1"
                                          er/*url*
                                          er/*old-index*
                                          es-type)
                                  {:throw-exceptions false})
                        :status)
          new-index-count (-> (http/get (format "%s/%s/_count"
                                               er/*url*
                                               er/*alias*))
                             :body
                             (json/parse-string true)
                             :count)]
      (testing "Old index no longer exists"
        (is (= 404 old-status)))
      (testing "New Index Count should be 2"
        (println new-index-count)
        (is (= 2 new-index-count))))))
