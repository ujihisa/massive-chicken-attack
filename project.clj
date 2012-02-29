(defproject massive-chicken-attack "1.0.0-SNAPSHOT"
  :description "Chicken revenges you!"
  :dependencies []
  :dev-dependencies [[org.bukkit/bukkit "1.1-R4"]
                     [clj-minecraft "1.0.0-SNAPSHOT"]
                     [org.clojure/clojure "1.3.0"]
                     [org.clojure/tools.logging "0.2.3"]]
  :repl-options [:init nil :caught clj-stacktrace.repl/pst+]
  :repositories {"spout-repo-snap" "http://repo.getspout.org/content/repositories/snapshots/"
                 "spout-repo-rel" "http://repo.getspout.org/content/repositories/releases/"})
