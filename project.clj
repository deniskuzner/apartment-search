(defproject clojure-project "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [enlive "1.1.6"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [ring/ring-json "0.5.0"]
                 [ring-cors "0.1.13"]
                 [metosin/ring-http-response "0.9.0"]
                 [compojure "1.2.0-SNAPSHOT"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [com.draines/postal "2.0.3"]]
  :repl-options {:init-ns clojure-project.core})
