(ns sourcemap-cljs.core
  (:require ["source-map" :as sourcemap :refer [SourceMapConsumer]]
            ["fs" :as fs]
            [clojure.string :as string]))

(enable-console-print!)

(defn parse-file
  [path]
  (.parse js/JSON (.readFileSync ^js fs path)))

(defn parse-args
  [args]
  (into {}
        (for [[k v] (partition 2 args)
              :let [[_ k] (re-matches #"\-\-(.*)" k)]
              :when k]
          [k v])))

(defn extract-locations
  [path]
  (let [f (.readFileSync fs path "utf8")]
    (re-seq #"\d+:\d+" f)))

(defn main
  [& args]
  (let [{:strs [match locations stacktrace js cljs] :as args} (parse-args args)
        _ (println args)
        _ (assert (and js cljs (or locations stacktrace)))
        lcs (if stacktrace
              (extract-locations stacktrace)
              (string/split locations #","))
        js-map (parse-file js)
        cljs-map (parse-file cljs)]
    (-> (reduce (fn [p lc]
                  (-> p
                      (.then (fn []
                               (let [[a l c] (re-matches #"(\d+):(\d+)" (string/trim lc))
                                     l (js/parseInt l)
                                     c (js/parseInt c)]
                                 (.with
                                   SourceMapConsumer
                                   js-map
                                   nil
                                   (fn [consumer]
                                     (let [pos ^js (.originalPositionFor ^js consumer #js {:line l
                                                                                   :column c})
                                           {:keys [line column]} (js->clj pos :keywordize-keys true)]
                                       (.with
                                         SourceMapConsumer
                                         cljs-map
                                         nil
                                         (fn [consumer]
                                           (let [pos2 (.originalPositionFor ^js consumer #js {:line (inc line)
                                                                                              :column column})
                                                 ret (js->clj pos2 :keywordize-keys true)]
                                             (when (or (nil? match)
                                                       (not (neg? (.indexOf (:source ret) match))))
                                               (println lc)
                                               (println ret)))))))))
                               ))))
                (.resolve js/Promise)
                lcs)
        (.catch (fn [err]
                  (println "An error occurred:")
                  (println err))))))
