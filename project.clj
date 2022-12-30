(defproject dlvrbot "0.1.1"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :min-lein-version "2.9.10"
  :dependencies [[org.clojure/clojure "1.11.1"]
                ;; [org.clojure/core.match "1.0.0"]

                 [compojure "1.7.0"]
                 [ring/ring-core "1.9.5"]
                 [ring/ring-jetty-adapter "1.9.5"]
                 [ring/ring-defaults "0.3.3"]
                 [ring/ring-json "0.5.1"]

                 [telegrambot-lib "2.2.0"]
                 [cheshire "5.11.0"]
                 [clj-time "0.15.2"]]
                ;; [clj-http "3.12.3"]
                ;; [clj-pdf "2.6.1"]

                ;; [org.clojure/java.jdbc "0.7.12"]
                ;; [org.postgresql/postgresql "42.4.1"]]

  :main ^:skip-aot tg-bot-framework.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
