(set-env!
 :resource-paths #{"src"}
 :dependencies '[[adzerk/bootlaces "0.1.13" :scope "test"]])

(require
  '[adzerk.bootlaces :refer :all]
  '[micha.boot-cp :refer :all])

(def +version+ "0.1.5")

(bootlaces! +version+)

(task-options!
 pom  {:project     'org.clojars.micha/boot-cp
       :version     +version+
       :description "Classpath task for the boot build tool."
       :url         "https://github.com/micha/boot-cp"
       :scm         {:url "https://github.com/micha/boot-cp"}
       :license     {"Eclipse Public License"
                     "http://www.eclipse.org/legal/epl-v10.html"}})
