(ns micha.boot-cp
  {:boot/export-tasks true}
  (:require
    [boot.pod :as pod]
    [boot.file :as file]
    [clojure.java.io :as io]
    [clojure.string :as string]
    [boot.pedantic :as pedantic]
    [boot.util :as util :refer [info warn]]
    [boot.core :as boot :refer [deftask with-pass-thru set-env!]]))

(defn- relativize
  [libdir path]
  (.getPath
    (if-not libdir
      (io/file path)
      (let [canonical-libdir (.getCanonicalFile (io/file libdir))]
        (io/file libdir (file/relative-to canonical-libdir path))))))

(defn- write-cp
  [out libdir dependencies]
  (when libdir (set-env! :local-repo libdir))
  (let [deps-env (update-in pod/env [:dependencies] #(or dependencies %))]
    (if-let [conflicts (not-empty (pedantic/dep-conflicts deps-env))]
      (throw (ex-info "Unresolved dependency conflicts." {:conflicts conflicts}))
      (let [resolved        (pod/resolve-dependency-jars deps-env)
            relative-paths  (map (partial relativize libdir) resolved)]
        (spit out (apply str (interpose ":" relative-paths)))))))

(defn- read-cp
  [in]
  (doseq [path (filter string? (string/split (slurp in) #":"))]
    (pod/add-classpath path)))

(deftask with-cp
  "Specify Boot's classpath in a file instead of as Maven coordinates.

  If the --in option is given this task will load JAR files from the manifest
  file at PATH, expecting its contents to be a java -cp compatible string of
  JAR file paths.

  If the --out option is given the manifest file will be written. Dependencies
  are resolved from Maven repositories and the paths to these JARs written as
  a java -cp compatible string to the output file PATH.

  The --dependencies option expects a vector of Maven coordinate vectors (the
  same as you'd provide to set-env! :dependencies. The default if this option
  is not given is to use the dependencies from the boot :dependencies. This
  option only applies in combination with the --out option described above.

  NOTE: An exception is thrown if there are unresolved dependency conflicts.

  The --libdir option specifies the PATH where the dependency JARs should be
  stashed. The default if this option is not given is to use the Maven local
  repository setting from the boot environment. This option only applies in
  combination with the --out option."

  [i in PATH          str "The classpath file from which to read the JAR paths."
   o out PATH         str "The classpath file to which the JAR paths will be written."
   l libdir PATH      str "The (optional) lib directory in which to stash resolved JARs."
   d dependencies EDN edn "The (optional) Maven dependencies to resolve and write to the classpath file."]

  (with-pass-thru [_]
    (cond out   (write-cp out libdir dependencies)
          in    (read-cp in)
          :else (warn "Either --in or --out expected, neither provided.\n"))))
