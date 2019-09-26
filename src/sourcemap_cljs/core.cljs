(ns sourcemap-cljs.core
  (:require ["source-map" :as sourcemap :refer [SourceMapConsumer]]
            ["fs" :as fs]))

(enable-console-print!)

(defn parse-file
  [path]
  (.parse js/JSON (.readFileSync fs path)))

(defn main
  [& args]
  (let [[l c p & [p2]] args
        l (js/parseInt l)
        c (js/parseInt c)
        m (parse-file p)]
    (.with
      SourceMapConsumer
      m
      nil
      (fn [consumer]
        (let [pos (.originalPositionFor consumer #js {:line l
                                                      :column c})
              line (.-line pos)
              column (.-column pos)]
          (println pos)
          (when p2
            (let [m2 (parse-file p2)]
              (.with
                SourceMapConsumer
                m2
                nil
                (fn [consumer]
                  (let [pos (.originalPositionFor consumer #js {:line line
                                                                :column column})]
                    (println pos)))))))))))
