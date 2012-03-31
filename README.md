# What is Route One

Route One is a tiny Clojure library that generates HTTP resource routes (as in Ruby on Rails, Jersey, Noir and similar
modern Web application frameworks). It is meant to be used in HTTP clients, programs that deliver emails with links,
testing environments and so on.

Route One is intentionally small and has very limited feature scope.


## Documentation & Examples

``` clojure
(ns my.app
  (:use [clojurewerkz.route-one.core :only [route-map route path-for named-path]]))

;; define a route map
(route-map
  (route "/about" :named "about page")
  (route "/faq")
  (route "/help"  :named "help page")
  (route "/docs/:title" :named "documents"))

;; generate relative paths
(path-for "/docs/title"  { :title "ohai" }) ;; => "/docs/title"
(path-for "/docs/:title" { :title "ohai" }) ;; => "/docs/ohai"
(path-for "/docs/:category/:title" { :category "greetings" :title "ohai" }) ;; => "/docs/greetings/ohai"
(path-for "/docs/:category/:title" { :category "greetings" }) ;; => IllegalArgumentException, because :title value is missing

(named-path "about page") ;; => "/about"
(named-path "documents" {:title "a-title"}) ;; => "/docs/a-title"

(with-base-url "https://myservice.com"
  (url-for "/docs/title"  { :title "ohai" }) ;; => "https://myservice.com/docs/title"
  (url-for "/docs/:title" { :title "ohai" }) ;; => "https://myservice.com/docs/ohai"
  (url-for "/docs/:category/:title" { :category "greetings" :title "ohai" }) ;; => "https://myservice.com/docs/greetings/ohai"
  (url-for "/docs/:category/:title" { :category "greetings" }) ;; => IllegalArgumentException, because :title value is missing

  (named-url "about page") ;; => "https://myservice.com/about"
  (named-url "documents" {:title "a-title"}) ;; => "https://myservice.com/docs/a-title"
)
```

Documentation site for Urly is coming in the future (sorry!). Please see our test suite for more code examples.


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


## Route One Is a ClojureWerkz Project

Route One is part of the group of libraries known as ClojureWerkz, together with
[Neocons](https://github.com/michaelklishin/neocons), [Monger](https://github.com/michaelklishin/monger), [Langohr](https://github.com/michaelklishin/langohr), [Elastisch](https://github.com/clojurewerkz/elastisch), [Quartzite](https://github.com/michaelklishin/quartzite), [Urly](https://github.com/michaelklishin/urly) and several others.


## Continuous Integration

[![Continuous Integration status](https://secure.travis-ci.org/clojurewerkz/route-one.png)](http://travis-ci.org/clojurewerkz/route-one)

CI is hosted by [travis-ci.org](http://travis-ci.org)


## Development

Route One uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make sure you have it installed and then run tests against Clojure 1.3.0 and 1.4.0[-beta6] using

    lein2 all test

Then create a branch and make your changes on it. Once you are done with your changes and all tests pass, submit
a pull request on Github.



## License

Copyright © 2012 Michael S. Klishin, Alex Petrov

Distributed under the Eclipse Public License, the same as Clojure.
