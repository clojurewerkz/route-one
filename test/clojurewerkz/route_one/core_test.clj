(ns clojurewerkz.route-one.core-test
  (:use clojure.test
        clojurewerkz.route-one.core))

(defroute about "/about")
(defroute faq "/faq")
(defroute help "/help")
(defroute documents "/docs/:title")
(defroute category-documents "/docs/:category/:title")
(defroute documents-with-ext "/docs/:title.:ext")

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
    (is (= "/about" (about-path))))
  (testing "generation of named routes with segments"
    (is (= "/docs/cat/a-title" (category-documents-path :title "a-title" :category "cat")))
    (is (= "/docs/a-title" (documents-path :title "a-title"))))
  (testing "generation of named routes with segments"
    (is (= "/docs/a-title.txt" (documents-with-ext-path :title "a-title" :ext "txt")))))

(deftest test-url-generation
  (with-base-url "http://giove.local"
    (testing "generation of routes w/o segments"
      (is (= "http://giove.local/about"         (url-for "/about" {})))
      (is (= "http://giove.local/about/project" (url-for "/about/project" {})))))
  (with-base-url "http://giove.local"
    (testing "generation of routes w/o segments"
      (is (= "http://giove.local/about"         (about-url)))))
  ;; really broken input
  (with-base-url "HTTP://https://API.MYAPP.COM/"
    (testing "generation of routes with segments"
      (is (= "https://api.myapp.com/clojurewerkz/route-one" (url-for "/:organization/:project" {:organization "clojurewerkz" :project "route-one"}))))))

(deftest test-templates
  (is (= "/about" about-template))
  (is (= "/faq" faq-template))
  (is (= "/help" help-template))
  (is (= "/docs/:title" documents-template))
  (is (= "/docs/:category/:title" category-documents-template))
  (is (= "/docs/:title.:ext" documents-with-ext-template)))
