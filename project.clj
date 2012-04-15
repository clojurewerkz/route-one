(defproject clojurewerkz/route-one "1.0.0-SNAPSHOT"
  :description "A tiny URL/route generation library"
  :url "http://github.com/clojurewerkz/route-one"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [clojurewerkz/urly   "1.0.0-rc2"]]
  :source-paths ["src/clojure"]
  :profiles {:1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}}
  :aliases { "all" ["with-profile" "dev:dev,1.4"] }
  :repositories {"clojure-releases" "http://build.clojure.org/releases"
                 "sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}}
  :warn-on-reflection true)
