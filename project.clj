(defproject org.clojars.jj/embedded-ldap-clj  "1.0.1-SNAPSHOT"
  :description "Embedded ldap server"
  :url "https://github.com/ruroru/embedded-ldap-clj"
  :license {:name "EPL-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.3"]
                 [com.unboundid/unboundid-ldapsdk "7.0.4"]]

  :deploy-repositories [["clojars" {:url      "https://repo.clojars.org"
                                    :username :env/clojars_user
                                    :password :env/clojars_pass}]]

  :plugins [[org.clojars.jj/bump "1.0.4"]
            [org.clojars.jj/bump-md "1.1.0"]
            [org.clojars.jj/strict-check "1.1.0"]])
