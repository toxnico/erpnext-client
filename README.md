# erpnext-client

A Clojure library designed to interface Clojure programs with [ERPNext](https://github.com/frappe/erpnext) REST API.

## Usage

[![Clojars Project](https://img.shields.io/clojars/v/erpnext-client.svg)](https://clojars.org/erpnext-client)

Available functions :

```clojure
;Initialize the authentication atoms
(login! "http://erpnext-host-base-url" "username" "password")

;Then, get some documents:
(erp-get "Customer")

;add filters and other querystring parameters:
(erp-get "Customer" {:filters [["customer_group" "=" "the group"]
                               ["default_currency" "=" "USD"]
                     :fields ["name" "default_price_list" "customer_type"]
                     :limit_page_length 45})

;create a new document
(erp-post! "the-doc-type" {:field1 "value1"
                           :field2 "value2"})

;update an existing document
(erp-put! "the-doc-type" "the-doc-name" {:field1 "value1"
                                         :field2 "value2"})

;delete a document
(erp-delete! "the-doc-type" "the-doc-name")
 
;submit a document
(erp-submit-document! "the-doc-type" "the-doc-name")

;send an arbitrary RPC request (body being provided as a map):
(erp-rpc-post! "the-method" body) 

```

## License

Copyright © 2019 

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
