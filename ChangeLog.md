## Changes Between 1.0.0-rc3 and 1.0.0

### url-for Preserves Paths From Base URL

Leading path component from `*base-url*` is now preserved when
constructing path in `url-for`.

Contributed by Ray Miller.


## Changes Between 1.0.0-rc2 and 1.0.0-rc3

### Catch All Route

`clojurewerkz.route-one.compojure/catch-all` is a new helper macro that
generates a route that matches any path.

It is useful for defining fallback routes (such as 404 or 500 status pages).


## Changes Between 1.0.0-rc1 and 1.0.0-rc2

### Tight Compojure integration

It is now possible to define named Compojure routes with Route One:

```clj
(ns my-app
  (:require [compojure.core :as compojure :as compojure])
  (:use clojurewerkz.route-one.compojure))

(compojure/defroutes main-routes
  (GET about request (handlers.root/root-page request)) ;; will use /about as a template
  (GET documents request (handlers.root/documents-page request)) ;; will use /documents as a template)
```

This will generate `main-routes` in same exact manner Compojure
generates them, but will also add helper functions for building urls
(`about-path`, `about-url`, `documents-path`, `document-url` and so
on).

To use this feature, you'll have to bring in Compojure as a dependency
to your project:

```clj
[compojure "1.1.5"]
```


## 1.0.0-rc1

Initial release we consider stable.
