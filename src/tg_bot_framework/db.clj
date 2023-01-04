(ns tg-bot-framework.db
  (:require [neo4j-clj.core :as n4j])
  (:import (java.net URI)))

(def neo4j
  (n4j/connect (URI. "neo4j+s://80aeb8f5.databases.neo4j.io:7687")
              "neo4j"
              "S3pQFBcytBqTliendc-En714sdia5I3QelWRe8tdT0Y"))

(n4j/defquery q-get-user-roles
  "MATCH (p:Person {chat_id: $chat_id})-[:HAS_ROLE]->(r:Role) RETURN r.id AS id")

(defmacro WS [body]
  `(with-open [~'session (n4j/get-session neo4j)]
     (doall ~body)))

(defn get-user-roles
  [chat-id]
  (WS (map (comp keyword :id) (q-get-user-roles session {:chat_id chat-id}))))

(defn get-user-state
  "TODO: Fixme"
  [chat-id]
  {:point "START"
   :variables {:a "A"}})
