(ns tg-bot-framework.handler
  (:require [tg-bot-framework.actions :as act]))

(defmacro structure []
  '{"START" {nil {:else act/send-ads}}})
