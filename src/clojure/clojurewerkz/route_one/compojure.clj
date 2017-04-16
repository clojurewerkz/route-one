(ns clojurewerkz.route-one.compojure
  (:require [compojure.core :as compojure]
            [clojure.tools.macro :refer (name-with-attributes)]
            [clojurewerkz.route-one.core :as route-one]))

(def
  ^{:dynamic true
    :doc "Serves for holding the path prefix in thread local binding,
          in context of which routes are evaluated. Makes it possible
          to pass context path to route definitions."}
  *route-prefix* nil)

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

(defn ^{:private true} evaluate-route-thunk
  [form]
  `(let [routes-thunk?# (::routes-thunk? (meta ~form))]
     (if routes-thunk?#
       (~form)
       ~form)))

(defmacro routes
  "Create a Ring handler by combining several handlers into one.

  NOTE: Contrary to its' compojure counterpart, this function returns
  a thunk (a function without arguments). The thunk is implicitly
  evaluated by route-one's versions of `context` and `defroutes`.
  Other important difference is the fact that it's a macro - which
  hinders composability."
  [& handlers]
  `(vary-meta
     (fn []
       (compojure/routes ~@(map evaluate-route-thunk handlers)))
     assoc ::routes-thunk? true))

(defmacro context
 "Give all routes in the form a common path prefix and set of bindings.
 If non-empty, `*route-prefix*` is put as a prefix to the given path.
 Thunks (functions without arguments) created by route-one's version of
 `routes` are implicitly evaluated.

  The following example demonstrates defining two routes with a common
  path prefix ('/user/:id') and a common binding ('id'):

    (context \"/user/:id\" [id]
      (GET \"/profile\" [] ...)
      (GET \"/settings\" [] ...))"
  [path args & routes]
  `(let [path# (if *route-prefix* (str *route-prefix* ~path) ~path)]
     (binding [*route-prefix* path#]
       (let [routes# ((routes ~@routes))]
         (compojure/context ~path ~args routes#)))))

(defmacro defroutes
  "Define a Ring handler function from a sequence of routes. The name may
  optionally be followed by a doc-string and metadata map. Thunks (functions
  without arguments) created by route-one's version of `routes` are implicitly
  evaluated."
  [name & routes]
  (let [[name routes] (name-with-attributes name routes)]
    `(def ~name ((routes ~@routes)))))
