(ns app.events
  (:require [re-frame.core :as rf]
            [app.swarm :as swarm]))

(def default-profile-picture "728e022bb638973be1fdf3e0e6b9b4f854b600326288fcd333d3280a709a591b")

(rf/reg-event-db
  :initialize-db
  (fn [_ _]
    (js/console.log "#initialise")
    {:profile {:name "default" 
               :picture default-profile-picture
               :view "js"
               :following [{:name "A"} {:name "B"}]}
     :bzz-get {:hash default-profile-picture
               :callback #()}
     :view-stack (list :profile)}))

(rf/reg-event-db
  :set-contact
  (fn [db [_ contact]]
    (assoc db :contact contact)))

(rf/reg-event-fx
  :show-contact
  (fn [{:keys [db] :as cofx} [_ contact-hash]]
    {:db (-> db
             (update :view-stack #(conj % :contact)))
     :bzz-get {:hash contact-hash
               :callback (partial swarm/cb-contact-data contact-hash)}
     }))

(rf/reg-event-fx
  :bzz-get
  (fn [cofx [_ params]]
    {:bzz-get params}))

(rf/reg-event-fx
  :bzz-put
  (fn [cofx [_ params]]
    {:bzz-put params}))

(rf/reg-event-db
  :navigate-back
  (fn [db _]
    (update db :view-stack pop)))

(rf/reg-fx
  :bzz-get
  (fn [{:keys [hash callback]}]
    (swarm/download hash callback)))

(rf/reg-fx
  :bzz-put
  (fn [{:keys [hash data callback]}]
    (swarm/upload hash data callback)))
