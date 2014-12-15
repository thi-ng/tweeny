(defproject thi.ng/tweeny "0.1.0-SNAPSHOT"
  :description "Keyframe animation & tweening functions for Clojure"
  :url          "http://thi.ng/tweeny"
  :license      {:name "Apache Software License 2.0"
                 :url "http://www.apache.org/licenses/LICENSE-2.0"
                 :distribution :repo}
  :scm          {:name "git"
                 :url "git@github.com:thi-ng/tweeny.git"}

  :min-lein-vesion "2.4.0"

  :dependencies [[org.clojure/clojure "1.6.0"]]

  :profiles     {:dev {:dependencies [[org.clojure/clojurescript "0.0-2411"]]
                       :plugins [[com.keminglabs/cljx "0.5.0"]
                                 [lein-cljsbuild "1.0.4-SNAPSHOT"]
                                 [com.cemerick/clojurescript.test "0.3.1"]]
                       :global-vars {*warn-on-reflection* true}
                       :jvm-opts ^:replace []
                       :auto-clean false
                       :prep-tasks [["cljx" "once"] "javac" "compile"]
                       :aliases {"cleantest" ["do" "clean," "cljx" "once," "test," "cljsbuild" "test"]}}}

  :cljx         {:builds [{:source-paths ["src/cljx"]
                           :output-path "target/classes"
                           :rules :clj}
                          {:source-paths ["src/cljx"]
                           :output-path "target/classes"
                           :rules :cljs}
                          {:source-paths ["test/cljx"]
                           :output-path "target/test-classes"
                           :rules :clj}
                          {:source-paths ["test/cljx"]
                           :output-path "target/test-classes"
                           :rules :cljs}]}

  :cljsbuild    {:builds [{:source-paths ["target/classes" "target/test-classes"]
                           :id "simple"
                           :compiler {:output-to "target/tweeny-0.1.0-SNAPSHOT.js"
                                      :optimizations :whitespace
                                      :pretty-print true}}]
                 :test-commands {"unit-tests" ["phantomjs" :runner "target/tweeny-0.1.0-SNAPSHOT.js"]}}

  :pom-addition [:developers [:developer
                              [:name "Karsten Schmidt"]
                              [:url "http://postspectacular.com"]
                              [:timezone "0"]]])
