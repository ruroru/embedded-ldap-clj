(ns jj.embedded.ldap-test
  (:require
    [clojure.test :refer [deftest is]]
    [jj.embedded.ldap :as ldap])
  (:import (com.unboundid.ldap.listener InMemoryDirectoryServer)
           (com.unboundid.ldap.sdk Attribute Entry SearchRequest SearchScope)))

(defn get-all-entries
  [^InMemoryDirectoryServer server base-dn]
  (let [search-request (SearchRequest. ^String base-dn
                                       ^SearchScope SearchScope/SUB
                                       "(objectClass=*)"
                                       ^"[Ljava.lang.String;" (into-array String []))
        search-result (.search server search-request)]
    (map (fn [entry]
           {:dn         (.getDN entry)
            :attributes (->> (.getAttributes entry)
                             (map (fn [attr]
                                    {(.getName attr)
                                     (vec (.getValues attr))}))
                             (into {}))})
         (.getSearchEntries search-result))))

(defn add-entry!
  [^InMemoryDirectoryServer server dn attributes]
  (let [entry ^Entry (Entry. ^String dn ^"[Lcom.unboundid.ldap.sdk.Attribute;" (into-array Attribute attributes))]
    (.add server entry)))

(deftest start-and-stop-server
  (let [base-dn "dc=example,dc=com"
        server (ldap/start-ldap {:base-dn base-dn
                                 :port    8080})]
    (add-entry! server base-dn [(Attribute. "objectClass" ^"[Ljava.lang.String;" (into-array ["top" "domain"]))
                                (Attribute. "dc" "example")])

    (is (= (list {:attributes {"dc"          ["example"]
                               "objectClass" ["top"
                                              "domain"]}
                  :dn         "dc=example,dc=com"})
           (get-all-entries server base-dn)))
    (ldap/stop-ldap server)))


(deftest with-ldap
  (let [base-dn "dc=example,dc=com"
        server-config {:base-dn base-dn
                       :port    8080}]

    (ldap/with-ldap server-config (fn [server]
                                    (add-entry! server base-dn [(Attribute. "objectClass" ^"[Ljava.lang.String;" (into-array ["top" "domain"]))
                                                                (Attribute. "dc" "example")])
                                    (is (= (list {:attributes {"dc"          ["example"]
                                                               "objectClass" ["top"
                                                                              "domain"]}
                                                  :dn         "dc=example,dc=com"})
                                           (get-all-entries server base-dn)))))))