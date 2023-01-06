(ns tg-bot-framework.bot
  (:require [telegrambot-lib.core :as tbot]))

(def mybot (tbot/create "5960181038:AAEEItibEfSyeUjJruK1w7InuUwr3gsDLIE"))

(defn convert-symbols [ch]
  (let [s (str ch)]
    (cond
      (= s "{")
      "(array-map "
      (= s "}")
      ")"
      :else
      s)))

(defmacro TGBOT []
  (let [structure (let [s0 (slurp "src/tg_bot_framework/structure.clj")
                        s1 (do (println "S0\t" s0) (str s0))
                        s2 (do (println "S1\t" s1) (map convert-symbols s1))
                        s3 (do (println "S2\t" s2) (clojure.string/join s2))
                        s4 (do (println "S3\t" s3) (read-string s3))]
                    (println "S4\t" s4)
                    s4)
        cases (map (fn [state-point]
                     (map (fn [message]
                            (map (fn [callback-point]
                                   (do
                                   (println "STATE-POINT:\t" state-point "\tMESSAGE:\t" message "\tCALLBACK-POINT:\t" callback-point)
                                   [`(and
                                      ~(if (= :else state-point) true `(= (get-in (second [~@(keys &env)]) [:state :point]) ~state-point))
                                      ~(cond
                                         (= :else message) true
                                         (= :number message)
                                         `(try (boolean (. Integer parseInt (get-in (second [~@(keys &env)]) [:incoming :message :text]))) (catch Exception ~'e false))
                                         (= :text message)
                                         `(not (nil? (get-in (second [~@(keys &env)]) [:incoming :message :text])))
                                         (= :image message)
                                         `(not (nil? (get-in (second [~@(keys &env)]) [:incoming :message :photo])))
                                         (= :location message)
                                         `(not (nil? (get-in (second [~@(keys &env)]) [:incoming :message :location])))
                                         :else
                                         `(= (get-in (second [~@(keys &env)]) [:incoming :message :text]) ~message))
                                      ~(if (= :else callback-point) true `(= (get-in (second [~@(keys &env)]) [:incoming :callback_query :data :p]) ~callback-point)))
                                    `(~@(let [action (get-in structure [state-point message callback-point])
                                              point (first (filter string? action))
                                              role (first (filter keyword? action))
                                              path (first (filter vector? action))
                                              func (first (filter fn? action))]

                                          `(do ;;(log/debug (str "Matched condition: " ~state-point " " ~message " " ~callback-point))
                                               ;;(log/debug ~(str "Values: " point " - " role " - " path))
                                               ((get-in (second [~@(keys &env)]) [:ch-role])
                                                ~role (do (when (some? ~func) (apply ~func [(second [~@(keys &env)])]))
                                                          (when (some? ~point) (tg-bot-framework.core/reprocess
                                                                                (get-in (second [~@(keys &env)]) [:chat-id])
                                                                                (get-in (second [~@(keys &env)]) [:state])
                                                                                ~point
                                                                                ~path
                                                                                ~(cond
                                                                                   (nil? message)
                                                                                   `(get-in (second [~@(keys &env)]) [:incoming :callback_query :data :vs])
                                                                                   (= :number message)
                                                                                   `(. Integer parseInt (get-in (second [~@(keys &env)]) [:incoming :message :text]))
                                                                                   (= :image message)
                                                                                   `(:file_id (first (sort (comp - :width) (get-in (second [~@(keys &env)]) [:incoming :message :photo]))))
                                                                                   (= :location message)
                                                                                   `(let [~'loc (get-in (second [~@(keys &env)]) [:incoming :message :location])]
                                                                                      {:latitude (:latitude ~'loc)
                                                                                       :longitude (:longitude ~'loc)
                                                                                       :title (or (get-in (second [~@(keys &env)]) [:incoming :message :venue :title])
                                                                                                  "N/A")}) ;; TODO: Add optional geotag API
                                                                                   (= :text message)
                                                                                   `(get-in (second [~@(keys &env)]) [:incoming :message :text])))))))))]))
                                 (keys (get-in structure [state-point message]))))
                          (keys (get-in structure [state-point]))))
                   (keys structure))]

    `(do (log/debug (str "Processing: " (second [~@(keys &env)])))
         (cond
           ~@(for [case (mapcat identity (mapcat identity (mapcat identity cases)))]
               case)

           :else
           (throw (Exception. "No pattern!"))))))

