(ns app.views
  (:require [re-frame.core :as rf]
            [app.subs :as subs]
            [reagent.core :as r]))

(defn message-view []
  (let []
  [:div "Message view"]
  ))

(defn contact-view []
  (let [name @(rf/subscribe [:contact-name])]
  [:div "Contact " name]
  ))

(defn following-item [f]
  [:div {:style {:background-color "#dddddd"
                 :margin-bottom 4
                 :border-radius 4}
         :on-click #(rf/dispatch [:show-contact (:name f)])}
   (:name f)])

(defn profile-info []
  (let [info @(rf/subscribe [:profile-info])]
    [:div {:style {:border-width "1px" :border-color "black"}}
     "Profile "
     (:name info)
     [:div
      "Following"]
     [:div
      {:style {:display :flex
               :flex-direction :column
               }}
      (for [f (:following info)]
        ^{:key f} [following-item f])]
     ]))

(defn top-bar []
  (let [current-view-id @(rf/subscribe [:current-view-id])]
    (js/console.log "current-view-id" current-view-id)
    [:div {:style {:background-color "#00DD77"
                   :display :flex
                   :justify-content :flex-start
                 :height 50}
           }
     (when (#{:contact :message} current-view-id)
       [:div
        {:style {:margin-right 10}
         :on-click #(rf/dispatch [:navigate-back])}
        [:span {:style {:font-weight :bold}} "Back"]]
       )
     (case current-view-id
       :profile "Profile info"
       :contact "Contact info"
       :message "Message"
       "")
   ]))

(defn main-view []
  (let [current-view-id @(rf/subscribe [:current-view-id])]
    (case current-view-id
       :profile [profile-info]
       :contact [contact-view]
       :message [message-view]
       [:div])))




(defn home-page
  []
  [:div
   [top-bar]
   [main-view]])
