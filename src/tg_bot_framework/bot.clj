(ns tg-bot-framework.bot
  (:require [telegrambot-lib.core :as tbot]
            [tg-bot-framework.misc :as misc]))

(def mybot (tbot/create "5882949779:AAEZf6iLR8OEuUdS0YlnR1SgzLu0otUi9uw"))

(defmacro TGBOT [struct]
  (let [p-map {:update {} ;; ~(&env "upd")
               :chat-id 12345 ;; ~(&env "chat-id")
               :roles [] ;; ~(&env "roles")
               :state {}}] ;;~(&env "state")}]

    `(cond
       ~@(map (fn [state-point]
               (map (fn [message-text]
                      (map !(fn [callback-point]
                             `((and
                               ~(if (= :else state-point) true `(= (get-in ~p-map [:state :point]) state-point))
                               ~(if (= :else message-text) true `(= (get-in ~p-map [:update :message :text]) message-text))
                               ~(if (= :else callback-point) true `(= (get-in ~p-map [:update :callback_query :data :point]) callback-point)))
                              (~(get-in struct [state-point message-text callback-point]) ~p-map)))
                           (keys (get-in struct [state-point message-text]))))
                    (keys (struct state-point))))
             (keys struct))

       :else
       (do (println "BAD!")
           (throw (Exception. "No pattern!"))))))
