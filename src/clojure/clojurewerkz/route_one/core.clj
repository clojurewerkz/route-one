(ns clojurewerkz.route-one.core
  (:import clojurewerkz.urly.UrlLike)
  (:use [clojure.string :only [split join]]
        [clojurewerkz.urly.core :only [url-like]]))

;;
;; Implementation
;;

(defrecord Route [path name opts])

(def route-maps (atom []))
(def empty-route-map [])


(def ^{:constant true :private true}
  slash-re #"/")
(def ^{:constant true :private true}
  slash "/")

(defn- replace-segment
  [^String s data]
  (if (.startsWith s ":")
    (if-let [v (get data (keyword (.substring s 1)))]
      v
      (throw (IllegalArgumentException. (str "Segment " s " is not found in the map " data))))
    s))

(defn replace-segments
  "Replaces segments that start with a colon with respective values from the data map.

   Example: (\"/docs/title\" { :title \"ohai\" }) ;; => \"/docs/title\""
  [^String s data]
  (let [parts (split s slash-re)]
    (join slash (map #(replace-segment % data) parts))))


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
  (conj v (Route. path (:named opts) opts)))

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
