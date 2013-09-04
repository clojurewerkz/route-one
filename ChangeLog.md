## Changes Between 1.0.0-rc1 and 1.0.0-rc2

### Tight Compojure integration

You can define named Compojure routes with route-one now:

```clj
(ns my-app
  (:require [compojure.core :as compojure :as compojure])
  (:use clojurewerkz.route-one.compojure))

(compojure/defroutes main-routes
  (GET about request (handlers.root/root-page request)) ;; will use /about as a template
  (GET documents request (handlers.root/documents-page request)) ;; will use /documents as a template)
```

That will generate `main-routes` in same exact manner Compojure generates them, but will also add helper functions
for building urls (`about-path`, `about-url`, `documents-path`, `document-url` and so on). For that, you'll have to
bring in Compojure as a dependency yourself:

```clj
[compojure "1.1.5"]
```


## 1.0.0-rc1

Initial release we consider stable.
