(set-env!
 :resource-paths #{"src"}
 :dependencies '[[adzerk/bootlaces "0.1.13" :scope "test"]
                 [adzerk/clj-github-docs "0.1.1" :scope "test"]])

(require
  '[micha.boot-cp :refer :all]
  '[adzerk.bootlaces :refer :all]
  '[adzerk.clj-github-docs :refer :all])

(def +version+      "1.0.1")
(def +description+  "Classpath task for the Boot build tool.")
(def +scm-url+      "https://github.com/micha/boot-cp")

(bootlaces! +version+)

(task-options!
 pom  {:project     'org.clojars.micha/boot-cp
       :version     +version+
       :description +description+
       :url         +scm-url+
       :scm         {:url +scm-url+}
       :license     {"Eclipse Public License"
                     "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask docs
  []
  (with-pass-thru [_]
    (write-docs
      :ns micha.boot-cp
      :tag +version+
      :doc +description+

      (section
        "Tasks"
        with-cp)

      (section
        "Helper Functions"
        make-pod-cp))))
