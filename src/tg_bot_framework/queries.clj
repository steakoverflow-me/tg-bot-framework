(ns tg-bot-framework.queries
  (:require [neo4j-clj.core :refer [defquery]]))

(defquery get-user-roles
  "MATCH (p:Person {chat_id: $chat_id})-[:HAS_ROLE]->(r:Role) RETURN r.id AS id")

(defquery get-dishes-list
  "MATCH (dc:DishCategory)<-[:HAS_CATEGORY]-(ds:Dish)-[:HAS_STATUS]->(s:Status) RETURN ds{.*, status_pict: s.pict, dish_category_pict: dc.pict} ORDER BY ds.name")

(defquery get-user-state
  "MATCH (p:Person {chat_id: $chat_id}) RETURN p.state AS state")

(defquery set-user-state
  "MATCH (p:Person {chat_id: $chat_id}) SET p.state = $state")
