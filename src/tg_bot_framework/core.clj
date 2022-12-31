(ns tg-bot-framework.core
  (:require [compojure.core :as cmpj :refer [POST]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :as resp]
            [ring.middleware
             [defaults :as rmd]
             [json :refer [wrap-json-body wrap-json-response]]]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [cheshire.core :as json]
            [telegrambot-lib.core :as tbot]
            [tg-bot-framework.db :as db]
            [tg-bot-framework.bot :refer [mybot TGBOT]]
            [tg-bot-framework.dictonary :as dict]
            [tg-bot-framework.actions :as act]
            [tg-bot-framework.handler :as h]))

(defn handle
  "The main fn for an incoming `Update` object processing."
  [upd-raw]
  (println "------------>")
  (let [upd (if (some? (get-in upd-raw [:callback_query :data]))
              (update-in upd-raw [:callback_query :data] #(json/parse-string % true))
              upd-raw)
        chat-id (or (get-in upd [:message :chat :id]) (get-in upd [:callback_query :from :id]))
        roles (db/get-user-roles chat-id)
        state (db/get-user-state chat-id)]
   ;; (TGBOT (eval h/struct))))
    (TGBOT {"START" {nil {:else #(act/send-ads %)}}})))


(def api-routes
  (POST "/" {update :body}
        (let [op-result (handle update)]
          (resp/response ""))))

(cmpj/defroutes
  app

  (-> (rmd/wrap-defaults api-routes rmd/api-defaults)
      (wrap-json-body {:keywords? true})
      (wrap-json-response)))

;; FOR DEV

(defonce update-id (atom nil))

(defn set-id!
  "Sets the update id to process next as the the passed in `id`."
  [id]
  (reset! update-id id))

(defn poll-updates
  "Long poll for recent chat messages from Telegram."
  ([bot]
   (poll-updates bot nil))

  ([bot offset]
   (let [resp (tbot/get-updates bot {:offset offset
                                     :timeout 10})]
     (if (contains? resp :error)
       (println "tbot/get-updates error:" (:error resp))
       resp))))

;; ^^^

(defn -main []
  ;;(let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
  ;;  (run-jetty app {:port port})))
  (loop []
    (let [updates (poll-updates mybot @update-id)
          messages (:result updates)]

      (println "LOOP STARTED!!!")
      ;; Check all messages, if any, for commands/keywords.
      (doseq [msg messages]
        (handle msg) ; your fn that decides what to do with each message.

        ;; Increment the next update-id to process.
        (-> msg
            :update_id
            inc
            set-id!))

      ;; Wait a while before checking for updates again.
      (Thread/sleep 3000))
    (recur)))
