(defproject clojurewerkz/route-one "1.2.0-SNAPSHOT"
  :description "A tiny URL/route generation library"
  :url "http://github.com/clojurewerkz/route-one"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clojurewerkz/urly   "2.0.0-alpha5"]
                 [ring/ring-codec     "1.0.0"]]
  :source-paths ["src/clojure"]
  :profiles {:dev {:dependencies [[compojure "1.1.8"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0-RC2"]]}
             :master {:dependencies [[org.clojure/clojure "1.7.0-master-SNAPSHOT"]]}}
  :aliases {"all" ["with-profile" "dev:dev,1.5:dev,1.7:dev,master"]}
  :repositories {"sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}}
  :global-vars {*warn-on-reflection* true})
