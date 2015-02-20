(ns clojurewerkz.route-one.compojure
  (:require [compojure.core :as compojure]
            [clojure.tools.macro :refer (name-with-attributes)]
            [clojurewerkz.route-one.core :as route-one]))

(def ^{:dynamic true} *route-prefix* nil)

(defmacro handler-with-route
  [req-handler name path args body]
  `(do
     (let [route-path# (if *route-prefix* (str *route-prefix* ~path) ~path)]
       (route-one/defroute ~(symbol (str name)) route-path#))
     (~req-handler ~path ~args ~@body)))

(defmacro GET "Generate a GET route."
  [name path args & body]
  `(handler-with-route compojure.core/GET ~name ~path ~args ~body))

(defmacro POST "Generate a POST route."
  [name path args & body]
  `(handler-with-route compojure.core/POST ~name ~path ~args ~body))

(defmacro PUT "Generate a PUT route."
  [name path args & body]
  `(handler-with-route compojure.core/PUT ~name ~path ~args ~body))

(defmacro OPTIONS "Generate a OPTIONS route."
  [name path args & body]
  `(handler-with-route compojure.core/OPTIONS ~name ~path ~args ~body))

(defmacro HEAD "Generate a HEAD route."
  [name path args & body]
  `(handler-with-route compojure.core/HEAD ~name ~path ~args ~body))

(defmacro PATCH "Generate a PATCH route."
  [name path args & body]
  `(handler-with-route compojure.core/PATCH ~name ~path ~args ~body))

(defmacro ANY "Generate a ANY route."
  [name path args & body]
  `(do
     (route-one/defroute ~(symbol (str name)) ~path)
     (compojure/ANY ~path ~args ~@body)))

(defmacro catch-all
  "Generate a Catch-all fallback route"
  [args & body]
  `(compojure/ANY "*" ~args ~@body))

(defn- evaluate-thunk
  [form]
  `(let [routes-thunk?# (:routes-thunk? (meta ~form))]
     (if routes-thunk?#
       (~form)
       ~form)))

(defmacro routes
  [& handlers]
  `(vary-meta
     (fn []
       (compojure/routes ~@(map evaluate-thunk handlers)))
     assoc :routes-thunk? true))

(defmacro context
  [path args & routes]
  `(let [path# (if *route-prefix* (str *route-prefix* ~path) ~path)]
     (binding [*route-prefix* path#]
       (let [routes# ((routes ~@routes))]
         (compojure/context ~path ~args routes#)))))

(defmacro defroutes
  [name & routes]
  (let [[name routes] (name-with-attributes name routes)]
    `(def ~name ((routes ~@routes)))))
