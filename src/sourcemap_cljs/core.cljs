(ns sourcemap-cljs.core
  (:require ["source-map" :as sourcemap :refer [SourceMapConsumer]]
            ["fs" :as fs]
            [clojure.string :as string]))

(enable-console-print!)

(defn parse-file
  [path]
  (.parse js/JSON (.readFileSync fs path)))

(defn main
  [& args]
  (let [[lcs p1 p2 & [all?]] args
        m1 (parse-file p1)
        m2 (parse-file p2)
        all? (some? all?)]
    (-> (reduce (fn [p lc]
                  (-> p
                      (.then (fn []
                               (let [[a l c] (re-matches #"(\d+):(\d+)" (string/trim lc))
                                     l (js/parseInt l)
                                     c (js/parseInt c)]
                                 (.with
                                   SourceMapConsumer
                                   m1
                                   nil
                                   (fn [consumer]
                                     (let [pos (.originalPositionFor consumer #js {:line l
                                                                                   :column c})
                                           line (.-line pos)
                                           column (.-column pos)]
                                       (.catch (.with
                                                 SourceMapConsumer
                                                 m2
                                                 nil
                                                 (fn [consumer]
                                                   (let [pos2 (.originalPositionFor consumer #js {:line (inc line)
                                                                                                  :column column})
                                                         ret (js->clj pos2 :keywordize-keys true)]
                                                     (when (or all? (.startsWith (:source ret) "riverford"))
                                                       (println lc)
                                                       (println ret)))))
                                               (fn []))))))
                               ))))
                (.resolve js/Promise)
                (string/split lcs #","))
        (.catch (fn []
                  (println "An error occurred"))))))
