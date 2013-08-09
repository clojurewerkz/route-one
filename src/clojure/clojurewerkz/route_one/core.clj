(ns clojurewerkz.route-one.core
  (:import clojurewerkz.urly.UrlLike)
  (:require [clojure.set :as cs])
  (:use [clojure.string :only [split join]]
        [clojurewerkz.urly.core :only [url-like]]))

;;
;; Implementation
;;

(defrecord Route [path name helper opts])

(def route-maps (atom []))
(def empty-route-map [])

(def ^{:constant true :private true}
  slash-re #"/")
(def ^{:constant true :private true}
  slash "/")


(defn validate-keys
  [^String s data]
  (let [required-parts (->> (split s #"/|\.")
                            (filter #(.startsWith % ":"))
                            (map #(keyword (.substring % 1)))
                            set)
        existing-parts (set (keys data))
        diff (cs/difference required-parts existing-parts)]
    (when (not (empty? diff))
      (throw
       (IllegalArgumentException.
        (str "Following segments are missing in the map " data ": " diff))))))

(defn replace-segments
  "Replaces segments that start with a colon with respective values from the data map.

   Example: (\"/docs/title\" { :title \"ohai\" }) ;; => \"/docs/title\""
  [^String s data]
  (validate-keys s data)
  (reduce (fn [acc [k replacement]]
            (let [pattern (re-pattern (str ":" (name k)))]
              (clojure.string/replace acc
                                      pattern
                                      (str replacement))))
          s data))


;;
;; API
;;

(def ^{:dynamic true} *base-url*)

(defn route
  "Defines an individual route.

   Example:

   (route \"/about\" :named \"about page\")
   (route \"/faq\")
   (route \"/help\"  :named \"help page\")
   (route \"/docs/:title\" :named \"documents\")"
  [v path &{ :as opts }]

  (when (:helper opts)
    (intern *ns* (symbol (str (name (:helper opts)) "-path"))
            (fn [& data]
              (replace-segments path (first data)))))

  (conj v (Route. path (:named opts) (:helper opts) opts)))

(defn add-route!
  "Add a route to a route-map"
  ([path opts]
     (add-route! route-maps path opts))
  ([a path opts]
     (dosync
      (swap! a
             #(apply route %
                     path
                     (apply concat opts))))))

(defmacro route-map
  "Defines a route map.

   Example:

   (route-map
     (route \"/about\" :named \"about page\")
     (route \"/faq\")
     (route \"/help\"  :named \"help page\")
     (route \"/docs/:title\" :named \"documents\"))"
  [& routes]
  `(let [coll# (-> empty-route-map ~@routes)]
     (reset! route-maps coll#)))

(defn path-for
  "Generates a regular path, replacing segments that start with a colon with respective values from the data map"
  [^String s data]
  (replace-segments s data))

(defn named-path
  "Like path-for but generates named paths"
  ([^String s]
     (if-let [p (some (fn [r] (when (= s (:name r))
                                r)) @route-maps)]
       (:path p)
       (throw (IllegalArgumentException. (str "Route with name " s " is not found")))))
  ([^String s data]
     (let [path (named-path s)]
       (replace-segments path data))))


(defmacro with-base-url
  [s & body]
  `(binding [*base-url* ~s]
     ~@body))

(defn url-for
  "Like path-for but generates full URLs. Use together with with-base-url."
  [^String s data]
  (str (.mutatePath (url-like *base-url*) (path-for s data))))

(defn named-url
  "Like url-for but generates named paths. Use together with with-base-url."
  ([^String s]
     (str (.mutatePath ^UrlLike (url-like *base-url*) (named-path s))))
  ([^String s data]
     (str (.mutatePath ^UrlLike (url-like *base-url*) (named-path s data)))))
