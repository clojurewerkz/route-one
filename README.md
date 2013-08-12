# What is Route One

Route One is a tiny Clojure library that generates HTTP resource routes (as in Ruby on Rails, Jersey, Noir and similar
modern Web application frameworks). It is meant to be used in HTTP clients, programs that deliver emails with links,
testing environments and so on.

Route One is intentionally small and has very limited feature scope.

## Maven Artifacts

### The Most Recent Release

With Leiningen:

    [clojurewerkz/route-one "1.0.0-beta1"]

With Maven:

    <dependency>
      <groupId>clojurewerkz</groupId>
      <artifactId>route-one</artifactId>
      <version>1.0.0-beta1</version>
    </dependency>


## Supported Clojure versions

Route One is built from the ground up for Clojure 1.3+ and JDK 6+.


## Documentation & Examples

```clj
(ns my.app
  (:use [clojurewerkz.route-one.core))

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

Use your templates with Compojure/Clout (they're not present as a dependency, but we support same path structure):

```clj
(ns my-app
  (require [compojure.core :as compojure])
  (:use [clojurewerkz.route-one.core))

(defroute about "/about")
(defroute documents "/docs/:title")

(compojure/defroutes main-routes
  (compojure/GET about-template request (handlers.root/root-page request)) ;; will use /about as a template
  (compojure/GET documents-template request (handlers.root/documents-page request)) ;; will use /documents as a template
  (route/not-found "Page not found"))
```

Documentation site for Urly is coming in the future (sorry!). Please see our test suite for more code examples.



## Route One Is a ClojureWerkz Project

Route One is part of the group of libraries known as ClojureWerkz, together with
[Cassaforte](http://clojurecassandra.info), [Monger](http://clojuremongodb.info), [Langohr](http://clojurerabbitmq.info), [Elastisch](http://clojureelasticsearch.info), [Quartzite](http://clojurequartz.info), and several others.


## Continuous Integration

[![Continuous Integration status](https://secure.travis-ci.org/clojurewerkz/route-one.png)](http://travis-ci.org/clojurewerkz/route-one)

CI is hosted by [travis-ci.org](http://travis-ci.org)


## Development

Route One uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make sure you have it installed and then run tests
against all supported Clojure versions using

    lein2 all test

Then create a branch and make your changes on it. Once you are done with your changes and all tests pass, submit
a pull request on Github.



## License

Copyright © 2012-2013 Michael S. Klishin, Alex Petrov

Distributed under the Eclipse Public License, the same as Clojure.
