(ns app.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :profile-info
  (fn [db _]
    (:profile db)))

(rf/reg-sub
  :current-view-id
  (fn [db _]
    (peek (:view-stack db))))

(rf/reg-sub
  :contact-name
  (fn [db _]
    (:contact-name db)))
