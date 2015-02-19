(ns clojurewerkz.route-one.core
  (:import clojurewerkz.urly.UrlLike)
  (:require [clojure.set :as cs]
            [clojurewerkz.urly.core :as urly]
            [clojure.string :as str]
            [ring.util.codec :as codec]))

;;
;; Implementation
;;

(defrecord Route [path name helper opts])

(def ^{:dynamic true} *base-url*)

(def ^{:constant true :private true}
  slash-re #"/")
(def ^{:constant true :private true}
  slash "/")

(defn required-parts
  [^String s]
  (->> (str/split s #"/|\.")
       (filter #(.startsWith ^String % ":"))
       (map #(keyword (.substring ^String % 1)))
       set))

(defn validate-keys
  [^String s data]
  (let [existing-parts (set (keys data))
        diff (cs/difference (required-parts s) existing-parts)]
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
            (let [match (str ":" (name k))]
              (clojure.string/replace acc
                                      match
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
  "Generates a regular path, replacing segments that start with a
  colon with respective values from the data map. Any key/vals in `data`
  that don't appear as segments in `s` are appended as a query string."
  [^String s data]
  (let [path (replace-segments s data)
        query-keys (remove (required-parts s) (keys data))]
    (if (empty? query-keys)
      path
      (str path "?" (codec/form-encode (select-keys data query-keys))))))

(defn url-for
  "Like path-for but generates full URLs. Use together with with-base-url."
  [^String s data]
  (let [base-url   (urly/url-like *base-url*)
        base-path  (str/replace (urly/path-of base-url) #"/\Z" "")
        rel-path   (str/replace (replace-segments s data) #"\A/" "")
        url        (.mutatePath base-url (str base-path "/" rel-path))
        query-keys (remove (required-parts s) (keys data))]
    (if (empty? query-keys)
      (str url)
      (str (urly/mutate-query url (codec/form-encode (select-keys data query-keys)))))))

(defmacro defroute
  {:arglists '([n pattern defaults?])}
  [^clojure.lang.Symbol n ^String pattern & [defaults]]
  `(do (def ~(symbol (format "%s-template" n)) ~pattern)
       (defn ~(symbol (format "%s-path" n)) [& data#]
         (let [data# (if (= 1 (count data#)) (first data#) (apply hash-map data#))]
           (path-for ~pattern (merge data# ~defaults))))
       (defn ~(symbol (format "%s-url" n)) [& data#]
         (let [data# (if (= 1 (count data#)) (first data#) (apply hash-map data#))]
           (url-for ~pattern (merge data# ~defaults))))))

(defmacro with-base-url
  [s & body]
  `(binding [*base-url* ~s]
     ~@body))
