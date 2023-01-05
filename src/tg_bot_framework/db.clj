(ns tg-bot-framework.db
  (:require [neo4j-clj.core :refer [connect get-session]]
            [clj-log4j2.core :as log]
            [cheshire.core :as json]
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

(defn get-user-state [chat-id]
  (WS (json/parse-string (q/get-user-state s {:chat_id chat-id}))))

(defn set-user-state [chat-id state]
  (WS (q/set-user-state s {:chat_id chat-id :state (json/generate-string state)})))

;; DISHES

(defn get-dishes-list []
  (WS (q/get-dishes-list s)))

