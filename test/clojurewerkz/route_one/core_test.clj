(ns clojurewerkz.route-one.core-test
  (:use clojure.test
        clojurewerkz.route-one.core))


(route-map
  (route "/about" :named "about page")
  (route "/faq")
  (route "/help"  :named "help page")
  (route "/docs/:title" :named "documents"))


(deftest test-replace-segments
  (testing "cases with all segments present in the data map"
    (are [path data output] (is (= output (replace-segments path data)))
         "/docs/title"  { :title "ohai" } "/docs/title"
         "/docs/:title" { :title "ohai" } "/docs/ohai"
         "/docs/:category/:title" { :category "greetings" :title "ohai" } "/docs/greetings/ohai"))
  (testing "cases with some segments missing from the data map"
    (are [path data] (is (thrown? IllegalArgumentException (replace-segments path data)))
         "/docs/:title" { :greeting "ohai" }
         "/docs/:category/:title" { :title "ohai" })))

(deftest test-path-generation
  (testing "generation of routes w/o segments"
    (is (= "/about"         (path-for "/about" {})))
    (is (= "/about/project" (path-for "/about/project" {}))))
  (testing "generation of routes with segments"
    (is (= "/clojurewerkz/route-one" (path-for "/:organization/:project" {:organization "clojurewerkz" :project "route-one"}))))
  (testing "generation of named routes w/o segments"
    (is (= "/about" (named-path "about page"))))
  (testing "generation of named routes with segments"
    (is (= "/docs/a-title" (named-path "documents" {:title "a-title"})))))

(deftest test-url-generation
  (with-base-url "http://giove.local"
    (testing "generation of routes w/o segments"
      (is (= "http://giove.local/about"         (url-for "/about" {})))
      (is (= "http://giove.local/about/project" (url-for "/about/project" {})))))
  ;; really broken input
  (with-base-url "HTTP://https://API.MYAPP.COM/"
    (testing "generation of routes with segments"
      (is (= "https://api.myapp.com/clojurewerkz/route-one" (url-for "/:organization/:project" {:organization "clojurewerkz" :project "route-one"}))))
    (testing "generation of named routes w/o segments"
      (is (= "https://api.myapp.com/about" (named-url "about page"))))
    (testing "generation of named routes with segments"
      (is (= "https://api.myapp.com/docs/a-title" (named-url "documents" {:title "a-title"}))))))
