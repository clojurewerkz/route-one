(ns clojurewerkz.route-one.compojure-test
  (:use clojure.test
        clojurewerkz.route-one.compojure))

(defn fake-request
  [method uri params]
  {:request-method method
   :uri            uri
   :params         params})

(deftest GET-test
  (testing "Root path"
    (let [handler (GET root "/" {:keys [params]}
                       (is (= {:get :params} params))
                       "res")]
      (is (= "res" (:body (handler
                           (fake-request :get (root-path) {:get :params})))))))
  (testing "Non-root path"
    (let [handler (GET users-index "/users" {:keys [params]}
                       (is (= {:get :params} params))
                       "res")]
      (is (= "res" (:body (handler
                           (fake-request :get (users-index-path) {:get :params}))))))))
