(ns clojurewerkz.route-one.core
  (:import clojurewerkz.urly.UrlLike)
  (:require [clojure.set :as cs]
            [clojurewerkz.urly.core :refer [url-like] :as urly]
            [clojure.string :refer [split]]))

;;
;; Implementation
;;

(defrecord Route [path name helper opts])

(def ^{:dynamic true} *base-url*)

(def ^{:constant true :private true}
  slash-re #"/")
(def ^{:constant true :private true}
  slash "/")

(defn validate-keys
  [^String s data]
  (let [required-parts (->> (split s #"/|\.")
                            (filter #(.startsWith ^String % ":"))
                            (map #(keyword (.substring ^String % 1)))
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

(defn route
  "Defines an individual route.

   Example:

   (route \"/about\" :named \"about page\")
   (route \"/faq\")
   (route \"/help\"  :named \"help page\")
   (route \"/docs/:title\" :named \"documents\")"
  [v path &{ :as opts }]

  (conj v (Route. path (:named opts) (:helper opts) opts)))

(defn path-for
  "Generates a regular path, replacing segments that start with a colon with respective values from the data map"
  [^String s data]
  (replace-segments s data))

(defn url-for
  "Like path-for but generates full URLs. Use together with with-base-url."
  [^String s data]
  (let [base-url (url-like *base-url*)
        base-path (clojure.string/replace (urly/path-of base-url) #"/\Z" "")
        rel-path  (clojure.string/replace (path-for s data) #"\A/" "")]
    (str (.mutatePath base-url (str base-path "/" rel-path)))))

(defmacro defroute
  [^clojure.lang.Symbol n ^String pattern]
  `(do (def ~(symbol (format "%s-template" n)) ~pattern)
       (defn ~(symbol (format "%s-path" n)) [& data#]
         (path-for ~pattern (apply hash-map data#)))
       (defn ~(symbol (format "%s-url" n)) [& data#]
         (url-for ~pattern (apply hash-map data#)))))

(defmacro with-base-url
  [s & body]
  `(binding [*base-url* ~s]
     ~@body))
