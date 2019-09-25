(ns sourcemap-cljs.core
  (:require ["source-map" :as sourcemap :refer [SourceMapConsumer]]
            ["fs" :as fs]))

(enable-console-print!)

(defn main
  [& args]
  (let [[p l c] args
        m (.parse js/JSON (.readFileSync fs p))
        l (js/parseInt l)
        c (js/parseInt c)]
    (.with
      SourceMapConsumer
      m
      nil
      (fn [consumer]
        (let [pos (.originalPositionFor consumer #js {:line l
                                                      :column c})]
          (println pos))))))
