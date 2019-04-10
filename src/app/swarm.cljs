(ns app.swarm
  (:require ["@erebos/swarm-browser" :as SwarmClient]
            ["web3" :as Web3]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            ["@erebos/keccak256" :refer [pubKeyToAddress]]
            ["@erebos/secp256k1" :refer [createKeyPair sign]]
            [clojure.edn :as edn]))

(def web3 (Web3. "ws://127.0.0.1:8546"))

(def keypair (createKeyPair))
(def user (pubKeyToAddress (-> keypair .getPublic .encode)))

(defn sign-bytes [bytes]
  (js/Promise. (fn [bytes]
                 (sign bytes (.getPrivate keypair)))))

(def bzz (SwarmClient/BzzAPI. #js {:url "http://localhost:8500"
                                   :signBytes sign-bytes}))

(def content (reagent/atom nil))
(def contenthash (reagent/atom "ad449d6934bc7481533f456b4eb59522cd514e16d7fee1c0c07ec06452e46951"))

(def messages (reagent/atom []))

(defn cb-message-data [resp-text]
               (let [message (edn/read-string resp-text)]
                 (swap! messages conj message)
                 (doseq [ancestor (:ancestors message)]
                   (rf/dispatch [:bzz-get ancestor]))))

(defn cb-contact-data [hash resp-text]
               (let [contact (edn/read-string resp-text)]
                 (rf/dispatch [:set-contact contact])))

(defn download [file callback]
  (-> bzz
      (.download file)
      (.then #(.text %)) ; TODO not only text, but also blob should be supported here
      (.then callback)))

(defn get-message-content [hash data]
  (pr-str {:ancestors (if hash [hash] [])
           :content data}))
                      
(defn upload [hash data callback]
  (-> bzz
      (.uploadFile 
        data
        #js {:contentType "text"})
      (.then callback)))

(def feedhash (atom nil))


#_(defn upload-feed-value [feedhash data]
    (.then (.uploadFeedValue bzz
                             feedhash
                             #js {"index.html" {:contentType "text/html"
                                                :data data}}
                             #js {:defaultPath "index.html"})
           (fn [res]
             (.log js/console res))))

#_(defn update-feed-value [feedhash data]
    (.then (.updateFeedValue bzz
                             feedhash
                             data)
           (fn [res]
             (.log js/console res))))

(defn upload-feed-value [feedhash data]
  (.then (.uploadFeedValue bzz
                           feedhash
                           data)
         (fn [res]
           (.log js/console res))))

(defn create-feed [name]
  (.then (.createFeedManifest bzz
                              #js {:name name
                                   :user user})
         (fn [hash]
           (reset! feedhash hash)
           (println "feedhash: " (.getFeedURL bzz hash))
           (upload-feed-value hash "potatoe potatoe"))))

(println user)
(create-feed "hello")

#_(.then (-> web3 .-eth .-personal (.sign "hello" "0x63410f8acabd08648c9230be91f87a24e7871616" "password")) println)
