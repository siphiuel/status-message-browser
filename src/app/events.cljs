(ns app.events
  (:require [re-frame.core :as rf]))


(rf/reg-event-db
  :initialize-db
  (fn [_ _]
    (js/console.log "#initialise")
    {:profile {:name "default" 
               :picture :hash 
               :view "js"
               :following [{:name "A"} {:name "B"}]}
     :view-stack (list :profile)}))

(rf/reg-event-db
  :show-contact
  (fn [db [_ contact-name]]
    (-> db
        (update :view-stack #(conj % :contact))
        (assoc :contact-name contact-name))))

(rf/reg-event-db
  :navigate-back
  (fn [db _]
    (update db :view-stack pop)))
