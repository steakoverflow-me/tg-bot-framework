(ns tg-bot-framework.db
  (:require [neo4j-clj.core :refer [connect get-session]]
            [clj-log4j2.core :as log]
            [tg-bot-framework.queries :as q])
  (:import (java.net URI)))

(def neo4j
  (connect (URI. "neo4j+s://80aeb8f5.databases.neo4j.io:7687")
              "neo4j"
              "S3pQFBcytBqTliendc-En714sdia5I3QelWRe8tdT0Y"))

(defmacro WS [body]
  `(with-open [~'s (get-session neo4j)]
     (log/debug (str "Neo4j request from: " '~@(next &form)))
     (let [~'result (into [] ~body)]
       (log/debug (str "Neo4j response: " ~'result))
       ~'result)))

;; ROLES

(defn get-user-roles [chat-id]
  (WS (map (comp keyword :id) (q/get-user-roles s {:chat_id chat-id}))))

;; STATE

(defn get-user-state
  "TODO: Fixme"
  [chat-id]
  {:point "START"
   :variables {:a "A"}})

;; DISHES

(defn get-dishes-list []
  (WS (q/get-dishes-list s)))

