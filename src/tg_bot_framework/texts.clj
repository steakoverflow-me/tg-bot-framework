(ns tg-bot-framework.texts
  (:require [tg-bot-framework.dictonary :as dict]))

(def approve "👍 Approve")

(def main-menu "🏠 Main menu")

(def dishes-list "🍲 Dishes list")

(def dishes-add "➕ Add dish")

(def dishes-activate (str (:pict (:active dict/statuses)) " Activate dish"))

(def dishes-disable (str (:pict (:disabled dict/statuses)) " Disable dish"))
