(ns tg-bot-framework.queries
  (:require [neo4j-clj.core :refer [defquery]]))

(defquery get-user-roles
  "MATCH (p:Person {chat_id: $chat_id})-[:HAS_ROLE]->(r:Role) RETURN r.id AS id")

(defquery get-statuses
  "MATCH (s:Status) RETURN *")

(defquery get-dish-categories
  "MATCH (dc:DishCategory) RETURN *")

(defquery get-dishes-list
  "MATCH (dc:DishCategory)<-[:HAS_CATEGORY]-(d:Dish)-[:HAS_STATUS]->(s:Status) RETURN d{.*, status_pict: s.pict, dish_category_pict: dc.pict} ORDER BY d.name")

(defquery get-dish
  "MATCH (s:Status)<-[:HAS_STATUS]-(d:Dish {uuid: $uuid})-[HAS_CATEGORY]->(dc:DishCategory) RETURN d{.*, status: s{.*}, dish_category: dc{.*}}")

(defquery create-dish "
MATCH (dc:DishCategory {id: $dish_category_id})
MATCH (s:Status {id: 'active'})
CREATE (d:Dish) SET d = $dish
MERGE (s)<-[:HAS_STATUS]-(d)-[:HAS_CATEGORY]->(dc)")

(defquery set-dish-status "
MATCH (d:Dish {uuid: $uuid})
MATCH (s:Status {id: $status_id})
OPTIONAL MATCH (d)-[r:HAS_STATUS]->(:Status)
MERGE (d)-[:HAS_STATUS]->(s)
DELETE r")

(defquery get-user-state
  "MATCH (p:Person {chat_id: $chat_id}) RETURN p.state AS state")

(defquery set-user-state
  "MATCH (p:Person {chat_id: $chat_id}) SET p.state = $state")
