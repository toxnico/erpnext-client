(defproject erpnext-client "0.1.0-SNAPSHOT"
  :description "Base layer for ERPNext interaction through its REST API"
  :url "https://github.com/toxnico/erpnext-client"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-http "3.10.0"]
                 [cheshire "5.8.1"]]
  :repl-options {:init-ns erpnext-client.core})
