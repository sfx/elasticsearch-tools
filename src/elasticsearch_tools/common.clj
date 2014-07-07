(ns elasticsearch-tools.common
  (:require [taoensso.timbre :as timbre]))

(defn log-resp
  [message response]
  (let [status (:status response)
        body (:body response)
        error (:error response)]
    (if (= status 200)
      (timbre/info (format "message: %s\n status: %s\n body: %s\n"
                           message status body))
      (timbre/error (format "message: %s\n status: %s\n error: %s\n"
                           message status error)))))

(defn now
  []
  (quot (System/currentTimeMillis) 1000))

(defn exit [status msg]
  (println msg)
  (System/exit status))
