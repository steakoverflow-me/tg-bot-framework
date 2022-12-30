(ns tg-bot-framework.dictonary
  (:require [tg-bot-framework.db :as db]
            [clojure.string :as str]))

;; (def roles (let [roles (into {} (map (juxt (comp keyword :id) :name) (db/get-roles)))]
;;              (println "Roles loaded:\t" (str/join ", " (vals roles)))
;;              roles))

;; (def order-statuses (let [statuses (into {} (map
;;                               (fn [status]
;;                                 {(keyword (:id status)) {:name (:name status)
;;                                                          :sign (:sign status)}})
;;                               (db/get-order-statuses)))]
;;                       (println "Order statuses loaded: " (str/join ", " (map :sign (vals statuses))))
;;                       statuses))
