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

;; STATUSES

(defn get-statuses []
  (map :s (WS (q/get-statuses s))))

;; ROLES

(defn get-user-roles [chat-id]
  (map (comp keyword :id) (WS (q/get-user-roles s {:chat_id chat-id}))))

;; DISH CATEGORIES

(defn get-dish-categories []
  (map :dc (WS (q/get-dish-categories s))))

;; STATE

(defn get-user-state [chat-id]
  (json/parse-string (:state (first (WS (q/get-user-state s {:chat_id chat-id})))) true))

(defn set-user-state [chat-id state]
  (log/debug (format "Set state for user %d: %s" chat-id state))
  (WS (q/set-user-state s {:chat_id chat-id :state (json/generate-string state)})))

;; DISHES

(defn get-dishes-list []
  (map :d (WS (q/get-dishes-list s))))

(defn get-dish [uuid]
  (:d (first (WS (q/get-dish s {:uuid uuid})))))

(defn create-dish [dish dish-category-id]
  (WS (q/create-dish s {:dish dish :dish_category_id dish-category-id})))

(defn set-dish-status [uuid status]
  (WS (q/set-dish-status s {:uuid uuid :status_id (name status)})))

