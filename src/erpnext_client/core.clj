(ns erpnext-client.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(def config (atom {:base "http://localhost:1180"
                   :rest "api/resource"
                   :rpc  "api/method"}))

(def auth-cookies (atom {}))

(defn make-app-url
  "Builds an ERPNext URL for application links"
  [doctype name]
  (format "%s/desk#Form/%s/%s" (:base @config) doctype name))

(defn make-url
  "Builds an ERPNext URL for REST API"
  ([doctype]
   (str (:base @config) "/" (:rest @config) "/" doctype))
  ([doctype name]
   (str (:base @config) "/" (:rest @config) "/" doctype "/" name)))

(defn format-parameter-map
  "Transforms map values into json.
  Used to serialize filters, fields and other URL parameters"
  [m]
  (->> m
       (map (fn [[k v]]
              {k (json/encode v)}))
       (apply merge)))

(defn response->data
  "Extracts the 'data' object from a response body"
  [response]
  (-> response
      :body
      (json/decode true)
      (get :data)))

(defn erp-get-by-name
  "Returns a complete document by its unique name"
  [doctype name]
  (let [url (make-url doctype name)]
    (->> (http/get url
                   {:cookies @auth-cookies})
         response->data)))

(defn erp-get
  "Sends a GET request and returns the content of 'data'"
  [doctype params]
  (let [url (make-url doctype)]
    (->> (http/get url
                   {:query-params (format-parameter-map params)
                    :cookies      @auth-cookies})
         response->data)))

(defn erp-get1
  "Returns the first item according to the filters"
  [doctype params]
  (->> (erp-get doctype params)
       (first)))

(defn erp-post!
  "Sends a POST request (create), accepts a map as request body"
  [doctype body]
  (let [url (make-url doctype)]
    (http/post url
               {:content-type :json
                :body         (cheshire.core/encode body)
                :cookies      @auth-cookies})))

(defn erp-put!
  "Sends a PUT request (update), accepts a map as request body"
  [doctype name body]
  (let [url (make-url doctype name)]
    (http/put url
              {:content-type :json
               :body         (cheshire.core/encode body)
               :cookies      @auth-cookies})))

(defn erp-delete!
  "Sends a DELETE request, expects a document name"
  [doctype name]
  (let [url (make-url doctype name)]
    (http/delete url
                 {:cookies @auth-cookies})))

(defn erp-delete-all!
  "Deletes ALL the documents of a given doctype"
  [doctype]
  (doseq [s (erp-get doctype {:limit_page_length 1000000
                              :fields            ["name"]})]
    (erp-delete! doctype (:name s))))

(defn erp-rpc-post!
  "Sends an RPC action request. Accepts a map as request body"
  [method body]
  (let [url (str (:base @config) "/" (@config :rpc) "/" method)]
    (http/post url
               {:content-type :json
                :body         (cheshire.core/encode body)})))

(defn erp-submit-document!
  "Submits a document by providing its doctype and name"
  [doctype name]
  ;retrieve the whole document
  (let [doc (erp-get-by-name doctype name)
        url (str (:base @config) "/" (@config :rpc) "/frappe.client.submit")]

    ;Send the POST request
    (http/post url
               {:content-type :json
                :body         (cheshire.core/encode {:doc doc})
                :cookies      @auth-cookies})))

(defn erp-login->cookies
  "Execute a login into the ERPNext server, returns the cookies in case of success"
  [user password]
  (-> (erp-rpc-post! "login"
                     {:usr user
                      :pwd password})
      :cookies))

(defn login!
  "Calls remote login and stores the base url
   and authentication cookies into the local atoms"
  [server user password]
  (println "Logging in")
  (reset! config (assoc @config :base server))
  (let [cookies (erp-login->cookies user password)]
    (reset! auth-cookies cookies)
    (println "Logged in as " user)))

