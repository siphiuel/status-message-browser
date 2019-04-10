(ns app.main
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [app.swarm :as swarm]
            [app.events :as events]
            [app.views :as views]
            [clojure.edn :as edn]))

#_(defn home-page
  []
  [:div
   (for [{:keys [content]} @messages]
     ^{:key content}[message-view content])])

(defn main! []
  (rf/dispatch [:initialize-db])
  (reagent/render [views/home-page]
                  (.getElementById js/document "app")))
