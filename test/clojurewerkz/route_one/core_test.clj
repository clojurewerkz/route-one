(ns clojurewerkz.route-one.core-test
  (:use clojure.test
        clojurewerkz.route-one.core))


(route-map
 (route "/about" :helper :about :named "about page")
 (route "/faq")
 (route "/help"  :helper :help :named "help page")
 (route "/docs/:title" :helper :documents :named "documents")
 (route "/docs/:title.:ext" :helper :documents-with-ext :named "documents-with-ext"))

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
    (is (= "/about" (about-path)))
    (is (= "/about" (named-path "about page"))))
  (testing "generation of named routes with segments"
    (is (= "/docs/a-title" (documents-path {:title "a-title"})))
    (is (= "/docs/a-title" (named-path "documents" {:title "a-title"}))))
  (testing "generation of named routes with segments"
    (is (= "/docs/a-title.txt" (documents-with-ext-path {:title "a-title" :ext "txt"})))
    (is (= "/docs/a-title.txt" (named-path "documents-with-ext" {:title "a-title" :ext "txt"}))))
  )

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

(deftest test-add-route
  (testing "adding routes to an atom passed as a parameter"
    (let [routes (atom empty-route-map)]
      (add-route! routes "/about" {:named "about page"})
      (is (= (count @routes) 1))))

  (testing "adding routes to the default route map"
    (dosync (reset! route-maps empty-route-map))
    (add-route! "/about" {:named "about page"})
    (is (= "/about" (named-path "about page")))))
