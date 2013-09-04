(ns clojurewerkz.route-one.compojure
  (:require [compojure.core :as compojure]
            [clojurewerkz.route-one.core :as route-one]))

(defmacro GET "Generate a GET route."
  [name path args & body]
  `(do
     (route-one/defroute ~(symbol (str name)) ~path)
     (compojure/GET ~path ~args ~@body)))

(defmacro POST "Generate a POST route."
  [name path args & body]
  `(do
     (route-one/defroute ~(symbol (str name)) ~path)
     (compojure/POST ~path ~args ~@body)))

(defmacro PUT "Generate a PUT route."
  [name path args & body]
  `(do
     (route-one/defroute ~(symbol (str name)) ~path)
     (compojure/PUT ~path ~args ~@body)))

(defmacro OPTIONS "Generate a OPTIONS route."
  [name path args & body]
  `(do
     (route-one/defroute ~(symbol (str name)) ~path)
     (compojure/OPTIONS ~path ~args ~@body)))

(defmacro HEAD "Generate a HEAD route."
  [name path args & body]
  `(do
     (route-one/defroute ~(symbol (str name)) ~path)
     (compojure/HEAD ~path ~args ~@body)))

(defmacro PATCH "Generate a PATCH route."
  [name path args & body]
  `(do
     (route-one/defroute ~(symbol (str name)) ~path)
     (compojure/PATCH ~path ~args ~@body)))

(defmacro ANY "Generate a ANY route."
  [name path args & body]
  `(do
     (route-one/defroute ~(symbol (str name)) ~path)
     (compojure/ANY ~path ~args ~@body)))
