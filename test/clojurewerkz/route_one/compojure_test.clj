(ns clojurewerkz.route-one.compojure-test
  (:require [compojure.core :as compojure])
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

(deftest nested-routes-test
  (testing "Handler in context"
    ((context "/user/current" []
               (GET user-profile "/profile" {:keys [params]} "res"))
      (fake-request :get "/user/current/profile" {:get :params}))
    (is (= (user-profile-path) "/user/current/profile")))
  (testing "Handler in nested contexts"
    (let [inner (routes (context "/tasks" []
                          (GET user-add-task "/add" {:keys [params]} "res")))
          outer (context "/user/current" [] (inner))]
      (is (= "res" (:body (outer (fake-request :get "/user/current/tasks/add" {:get :params})))))
      (is (= (user-add-task-path) "/user/current/tasks/add")))))
