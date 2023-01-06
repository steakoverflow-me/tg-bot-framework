(ns tg-bot-framework.bot
  (:require [telegrambot-lib.core :as tbot]))

(def mybot (tbot/create "5960181038:AAEEItibEfSyeUjJruK1w7InuUwr3gsDLIE"))

(defmacro TGBOT [structure]
  (let [cases (map (fn [state-point]
                     (map (fn [message]
                            (map (fn [callback-point]
                                   [`(and
                                      ~(if (= :else state-point) true `(= (get-in (second [~@(keys &env)]) [:state :point]) ~state-point))
                                      ~(cond
                                         (= :else message) true
                                         (= :number message)
                                         `(try (boolean (. Integer parseInt (get-in (second [~@(keys &env)]) [:incoming :message :text]))) (catch Exception e false))
                                         (= :text message)
                                         `(not (nil? (get-in (second [~@(keys &env)]) [:incoming :message :text])))
                                         (= :image message)
                                         `(not (nil? (get-in (second [~@(keys &env)]) [:incoming :message :photo])))
                                         (= :location message)
                                         `(not (nil? (get-in (second [~@(keys &env)]) [:incoming :message :location])))
                                         :else
                                         `(= (get-in (second [~@(keys &env)]) [:incoming :message :text]) ~message))
                                      ~(if (= :else callback-point) true `(= (get-in (second [~@(keys &env)]) [:incomi!ng :callback_query :data :point]) ~callback-point)))

                                    `(~@(let [action (get-in structure [state-point message callback-point])]
                                          (println "ACTION:\t" action)
                                          (if (vector? action)
                                            (let [point (first (filter string? action))
                                                  role (first (filter (keyword? action)))
                                                  path (first (filter (vector? action)))]
                                              (if (some? role)
                                                `((get-in (second [~@(keys &env)]) [:ch-role]) ~role (tg-bot-framework.core/reprocess (get-in (second [~@(keys &env)]) [:chat-id]) (get-in (second [~@(keys &env)]) [:state]) ~point ~path `(get-in (second [~@(keys &env)]) [:incoming :message :text])))
                                                `(tg-bot-framework.core/reprocess (get-in (second [~@(keys &env)]) [:chat-id]) (get-in (second [~@(keys &env)]) [:state]) ~point ~path `(get-in (second [~@(keys &env)]) [:incoming :message :text])))
                                              `(~action (second [~@(keys &env)]))))))])
                                 (keys (get-in structure [state-point message]))))
                          (keys (get-in structure [state-point]))))
                   (keys structure))]

    `(cond
       ~@(for [case (mapcat identity (mapcat identity (mapcat identity cases)))]
           case)

       :else
       (throw (Exception. "No pattern!")))))
