(ns tg-bot-framework.core
  (:require [compojure.core :as cmpj :refer [POST]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :as resp]
            [ring.middleware
             [defaults :as rmd]
             [json :refer [wrap-json-body wrap-json-response]]]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [clj-log4j2.core :as log]
            [cheshire.core :as json]
            [telegrambot-lib.core :as tbot]
            [tg-bot-framework.db :as db]
            [tg-bot-framework.bot :refer [mybot TGBOT]]
            [tg-bot-framework.dictonary :as dict]
            [tg-bot-framework.actions :as act]
            [tg-bot-framework.handler :as h]
            [tg-bot-framework.texts :as txts]))

(defmulti handle class)

(defn reprocess
  ([chat-id state-point] (reprocess chat-id nil state-point nil nil))
  ([chat-id state state-point var-path msg]
   (log/debug (str "Reprocessing with arguments: " chat-id state state-point var-path msg))
   (db/set-user-state chat-id {:point state-point :variables (cond
                                                               (and (some? var-path) (not (empty? var-path)) (nil? (first var-path)))
                                                               {}
                                                               (and (some? var-path) (not (empty? var-path)))
                                                               (assoc-in (:variables state) (try (. Integer parseInt msg) msg) var-path)
                                                               (and (some? var-path) (empty? var-path))
                                                               msg
                                                               :else
                                                               (:variables state))}
                      (handle chat-id))))

(defn prepare-update
  "Prepare update for macro &env"
  [upd-raw]
  (let [upd (if (some? (get-in upd-raw [:callback_query :data]))
              (update upd-raw [:callback_query :data] #(json/parse-string % true))
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

(defn convert-symbols [ch]
  (let [s (str ch)]
    (cond
      (= s "{")
      "(array-map "
      (= s "}")
      ")"
      :else
      s)))

(defmacro convert-to-array-maps [body]
  `(str/join (map convert-symbols (str '~body))))

(def structure (convert-to-array-maps {:else {txts/main-menu {:else ["START" [nil]]}}

      "START"       {txts/dishes-list {:else ["DISHES:LIST" :admin [nil]]}
                     :else            {:else [act/main-menu]}}

      "DISHES:LIST" {txts/dishes-add  {:else ["DISHES:ADD:NAME" :admin]}
                     nil              {"DISHES:VIEW" ["DISHES:VIEW" :admin []]}
                     :else            {:else [act/dishes-list]}}
      "DISHES:VIEW" {:else            {:else [act/dishes-view]}}
      "DISHES:ADD:NAME" {:text        {:else ["DISHES:ADD:DESCRIPTION" :admin [:name]]}
                         :else        {:else [act/dishes-add-name]}}
      "DISHES:ADD:DESCRIPTION" {:text {:else ["DISHES:ADD:PICTURE" :admin [:description]]}
                                :else {:else [act/dishes-add-description]}}
      "DISHES:ADD:PICTURE" {:image    {:else ["DISHES:ADD:PRICE" :admin [:picture]]}
                            :else     {:else [act/dishes-add-picture]}}
      "DISHES:ADD:PRICE" {:number     {:else ["DISHES:ADD:APPROVE" :admin [:price]]}
                          :else       {:else [act/dishes-add-price]}}
      "DISHES:ADD:APPROVE" {txts/approve {:else [act/dishes-add-approved "DISHES:LIST" :admin [nil]]}
                            :else        {:else [act/dishes-add-approve]}}}))

(defmethod handle clojure.lang.PersistentArrayMap [upd-raw]
  (log/debug (str "Handling update: " upd-raw))
  (let [tgbot-env (prepare-update upd-raw)]
    ;; TODO: Fixme!
    (TGBOT)
     ;; `(eval ~structure))))
     ;;(convert-to-array-maps
     ;;'{:els;; e {txts/main-menu {:else ["START" [nil]]}}

     ;;  "START"       {txts/dishes-list {:else ["DISHES:LIST" :admin [nil]]}
     ;;                 :else            {:else [act/main-menu]}}

     ;;  "DISHES:LIST" {txts/dishes-add  {:else ["DISHES:ADD:NAME" :admin]}
     ;;                 nil              {"DISHES:VIEW" ["DISHES:VIEW" :admin []]}
     ;;                 :else            {:else [act/dishes-list]}}
     ;;  "DISHES:VIEW" {:else            {:else [act/dishes-view]}}
     ;;  "DISHES:ADD:NAME" {:text        {:else ["DISHES:ADD:DESCRIPTION" :admin [:name]]}
     ;;                     :else        {:else [act/dishes-add-name]}}
     ;;  "DISHES:ADD:DESCRIPTION" {:text {:else ["DISHES:ADD:PICTURE" :admin [:description]]}
     ;;                            :else {:else [act/dishes-add-description]}}
     ;;  "DISHES:ADD:PICTURE" {:image    {:else ["DISHES:ADD:PRICE" :admin [:picture]]}
     ;;                        :else     {:else [act/dishes-add-picture]}}
     ;;  "DISHES:ADD:PRICE" {:number     {:else ["DISHES:ADD:APPROVE" :admin [:price]]}
     ;;                      :else       {:else [act/dishes-add-price]}}
     ;;  "DISHES:ADD:APPROVE" {txts/approve {:else [act/dishes-add-approved "DISHES:LIST" :admin [nil]]}
     ;;                        :else        {:else [act/dishes-add-approve]}}}
     ;; ))
))
;;)


;; (clojure.pprint/pprint (macroexpand-1 '(TGBOT {"START" {txts/dishes-list {:else ["DISHES:LIST"]} :else {:else act/main-menu}} "DISHES:LIST" {:else {:else act/dishes-list}}})))
