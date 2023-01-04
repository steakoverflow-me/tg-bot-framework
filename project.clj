(defproject dlvrbot "0.1.1"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :min-lein-version "2.9.10"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.logging "1.2.4"]

                 [compojure "1.7.0"]
                 [ring/ring-core "1.9.5"]
                 [ring/ring-jetty-adapter "1.9.5"]
                 [ring/ring-defaults "0.3.3"]
                 [ring/ring-json "0.5.1"]

                 [telegrambot-lib "2.2.0"]
                 [cheshire "5.11.0"]
                 [clj-time "0.15.2"]
                ;; [clj-http "3.12.3"]
                ;; [clj-pdf "2.6.1"]

                 [gorillalabs/neo4j-clj "4.1.2"]]

  :main ^:skip-aot tg-bot-framework.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                  "-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"]}})
