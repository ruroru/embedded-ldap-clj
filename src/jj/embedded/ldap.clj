(ns jj.embedded.ldap
  (:import (com.unboundid.ldap.listener InMemoryDirectoryServer InMemoryDirectoryServerConfig InMemoryListenerConfig)
           (java.util Collection Collections)))

(defn stop-ldap
  ([^InMemoryDirectoryServer server]
   (stop-ldap server true))
  ([^InMemoryDirectoryServer server graceful?]
   (.shutDown ^InMemoryDirectoryServer server graceful?)))

(defn start-ldap
  [{:keys [base-dn port]
    :or   {base-dn "dc=test,dc=com"
           port    8389}}]
  (let [ldap-config ^InMemoryListenerConfig (InMemoryListenerConfig/createLDAPConfig "default" port)
        server-config ^InMemoryDirectoryServerConfig (InMemoryDirectoryServerConfig. ^"[Ljava.lang.String;" (into-array String [base-dn]))]
    (.setListenerConfigs server-config ^Collection (Collections/singletonList ldap-config))

    (.addAdditionalBindCredentials server-config "cn=admin,dc=example,dc=com" "password")

    (let [server (InMemoryDirectoryServer. server-config)]
      server)))

(defn with-ldap [config f]
  (let [server (start-ldap config)]
    (try
      (f server)
      (catch Exception e
        (.printStackTrace ^Exception e))
      (finally (stop-ldap server)))))