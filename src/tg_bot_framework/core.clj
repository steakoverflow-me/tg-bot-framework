(ns tg-bot-framework.core
  (:require [cheshire.core :as json]
            [clj-log4j2.core :as log]
            [compojure.core :as cmpj :refer [POST]]
            (ring.middleware
              [defaults :as rmd]
              [json :refer [wrap-json-body wrap-json-response]])
            [ring.util.response :as resp]
            [telegrambot-lib.core :as tbot]
            [tg-bot-framework.actions :as act]
            [tg-bot-framework.bot :refer [TGBOT mybot]]
            [tg-bot-framework.db :as db]))

(defmulti handle class)

(defn reprocess
  ([chat-id state-point] (reprocess chat-id nil state-point nil nil))
  ([chat-id state state-point var-path msg]
   (log/debug (str "Reprocessing with arguments: " chat-id " " state " " state-point " " var-path " " msg))
   (db/set-user-state chat-id {:point state-point :variables (cond
                                                               (and (some? var-path) (not (empty? var-path)) (nil? (first var-path)))
                                                               {}
                                                               (and (some? var-path) (not (empty? var-path)))
                                                               (assoc-in (:variables state) var-path (try (. Integer parseInt msg) (catch Exception e msg)))
                                                               (and (some? var-path) (empty? var-path))
                                                               msg
                                                               :else
                                                               (:variables state))})
   (handle chat-id)))

(defn prepare-update
  "Prepare update for macro &env"
  [upd-raw]
  (let [upd (if (some? (get-in upd-raw [:callback_query :data]))
              (update-in upd-raw [:callback_query :data] #(json/parse-string % true))
              upd-raw)
        chat-id (or (get-in upd [:message :chat :id]) (get-in upd [:callback_query :from :id]))
        w-role (fn [role body] (when (some #{role} (db/get-user-roles chat-id)) body))
        ch-role (fn [role body] (if (or (nil? role) (some #{role} (db/get-user-roles chat-id))) body ((act/forbidden chat-id)(reprocess chat-id "START"))))
        state (db/get-user-state chat-id)]
    (log/debug (str "Got state of user " chat-id ": " state))
    {:incoming upd
     :chat-id chat-id
     :w-role w-role
     :ch-role ch-role
     :state state}))

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
       (log/error "Error in updates polling: " (:error resp))
       resp))))

;; ^^^

(defn -main []
  ;;(let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
  ;;  (run-jetty app {:port port})))
  (loop []
    (let [updates (poll-updates mybot @update-id)
          messages (:result updates)]
      (log/info "New updates polling loop iteration")
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

(defmethod handle java.lang.Long [chat-id]
  (handle (assoc-in {} [:message :chat :id] chat-id)))

(defmethod handle clojure.lang.PersistentArrayMap [upd-raw]
  (log/debug (str "Handling update: " upd-raw))
  (let [tgbot-env (prepare-update upd-raw)]
    (TGBOT)))

;; (clojure.pprint/pprint (macroexpand-1 '(TGBOT {"START" {txts/dishes-list {:else ["DISHES:LIST"]} :else {:else act/main-menu}} "DISHES:LIST" {:else {:else act/dishes-list}}})))
