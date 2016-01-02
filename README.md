# What is Route One

Route One is a tiny Clojure library that generates HTTP resource routes (as in Ruby on Rails, Jersey, Noir and similar
modern Web application frameworks). It is meant to be used in HTTP clients, programs that deliver emails with links,
testing environments and so on.

Route One is intentionally small and has very limited feature scope.

## Maven Artifacts

### The Most Recent Release

With Leiningen:

    [clojurewerkz/route-one "1.2.0"]

With Maven:

    <dependency>
      <groupId>clojurewerkz</groupId>
      <artifactId>route-one</artifactId>
      <version>1.2.0</version>
    </dependency>


## Supported Clojure versions

Route One requires Clojure 1.4+.


## Documentation & Examples

In order to define a route, you can use `defroute`:

```clj
(defroute document "/documents/:document-id")
```

After defining a route, you get several helper functions to work with the route:

`document-path` builds a relative path with passed params:

```clj
(document-path :document-id "123")
;; => "/documents/123"
```

`documents-url` builds an absolute url:

```clj
(with-base-url "https://myservice.com"
  (document-url :document-id "123"))
;; => "https://myservice.com/documents/123"
```

`document-template` gives a template that you can use to match the url in Clout/Compojure or any
other library that uses similar syntax.

```clj
document-template
;; => "/documents/:document-id"
```

### Usage examples


```clj
(ns my.app
  (:require [clojurewerkz.route-one.core :refer :all])

;; define your routes
(defroute about "/about")
(defroute faq "/faq")
(defroute help "/help")
(defroute documents "/docs/:title")
(defroute category-documents "/docs/:category/:title")
(defroute documents-with-ext "/docs/:title.:ext")

;; generate relative paths (by generated fns)
(documents-path :title "a-title") ;; => "/docs/a-title"
(documents-path :title "ohai") ;; => "/docs/ohai"

(path-for "/docs/:category/:title" { :category "greetings" :title "ohai" }) ;; => "/docs/greetings/ohai"
(path-for "/docs/:category/:title" { :category "greetings" }) ;; => IllegalArgumentException, because :title value is missing

(with-base-url "https://myservice.com"
  (url-for "/docs/title"  { :title "ohai" }) ;; => "https://myservice.com/docs/title"
  (url-for "/docs/:title" { :title "ohai" }) ;; => "https://myservice.com/docs/ohai"
  (url-for "/docs/:category/:title" { :category "greetings" :title "ohai" }) ;; => "https://myservice.com/docs/greetings/ohai"
  (url-for "/docs/:category/:title" { :category "greetings" }) ;; => IllegalArgumentException, because :title value is missing
)

;; generate relative paths (by generated fns)
(with-base-url "https://myservice.com"
  (documents-url :title "a-title") ;; => "https://myservice.com/docs/a-title"
  (documents-url :title "a-title" :category "greetings") ;; => "https://myservice.com/docs/greetings/a-title"
)
```

### Compojure

Use your templates with Compojure/Clout (they're not present as a dependency, but we support same path structure):

```clj
(ns my-app
  (:require [compojure.core :as compojure]
            [compojure.route :as route])
  (:use [clojurewerkz.route-one.core))

(defroute about "/about")
(defroute documents "/docs/:title")

(compojure/defroutes main-routes
  (compojure/GET about-template request (handlers.root/root-page request)) ;; will use /about as a template
  (compojure/GET documents-template request (handlers.root/documents-page request)) ;; will use /docs/:title as a template
  (route/not-found "Page not found"))
```

You can also use our overrides of Compojure functions that allow you to build named routes with Compojure:

```clj
(ns my-app
  (:require [compojure.core :as compojure])
  (:use clojurewerkz.route-one.compojure))

(compojure/defroutes main-routes
  (GET about "/about" request (handlers.root/root-page request)) ;; will use /about as a template
  (GET documents "/docs/:title" request (handlers.root/documents-page request)) ;; will use /docs/:title as a template)
```

That will generate `main-routes` in same exact manner Compojure generates them, but will also add helper functions
for building urls (`about-path`, `about-url`, `documents-path`, `document-url` and so on). For that, you'll have to
bring in Compojure as a dependency yourself:

```clj
[compojure "1.1.5"]
```

Documentation site for Route One is coming in the future (sorry!). Please see our test suite for more code examples.

## Route One Is a ClojureWerkz Project

Route One is part of the group of libraries known as ClojureWerkz, together with
[Cassaforte](http://clojurecassandra.info), [Monger](http://clojuremongodb.info), [Langohr](http://clojurerabbitmq.info), [Elastisch](http://clojureelasticsearch.info), [Quartzite](http://clojurequartz.info), and several others.


## Continuous Integration

[![Continuous Integration status](https://secure.travis-ci.org/clojurewerkz/route-one.png)](http://travis-ci.org/clojurewerkz/route-one)

CI is hosted by [travis-ci.org](http://travis-ci.org)


## Development

Route One uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make sure you have it installed and then run tests
against all supported Clojure versions using

    lein all test

Then create a branch and make your changes on it. Once you are done with your changes and all tests pass, submit
a pull request on Github.



## License

Copyright © 2012-2016 Michael S. Klishin, Alex Petrov

Distributed under the Eclipse Public License, the same as Clojure.
