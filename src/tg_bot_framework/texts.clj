(ns tg-bot-framework.texts
  (:require [tg-bot-framework.dictionary :as dict]))

(def approve "👍 Approve")

(def main-menu "🏠 Main menu")

(def dishes-list "🍲 Dishes list")

(def dishes-add "➕ Add dish")

(def dishes-edit "📝 Edit dish")

(def dishes-edit-name "Edit name")

(def dishes-edit-description "Edit description")

(def dishes-edit-picture "Edit picture")

(def dishes-edit-price "Edit price")

(def dishes-activate (str (:pict (:active dict/statuses)) " Activate dish"))

(def dishes-disable (str (:pict (:disabled dict/statuses)) " Disable dish"))
