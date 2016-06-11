(ns micha.boot-cp
  {:boot/export-tasks true}
  (:require
    [boot.pod         :as pod]
    [boot.file        :as file]
    [clojure.java.io  :as io]
    [clojure.string   :as string]
    [boot.pedantic    :as pedantic]
    [boot.util        :as util      :refer [info warn]]
    [boot.core        :as boot      :refer [deftask with-pass-thru set-env!]]))

(def my-id 'org.clojars.micha/boot-cp)

(defn- relativize
  [local-repo path]
  (.getPath
    (if-not local-repo
      (io/file path)
      (let [canonical-local-repo (.getCanonicalFile (io/file local-repo))]
        (io/file local-repo (file/relative-to canonical-local-repo path))))))

(defn- dep-conflicts
  [{:keys [dependencies exclusions] :as env}]
  (let [non-transitive?   (set (map first dependencies))
        global-exclusions (partial pod/apply-global-exclusions exclusions)]
    (into {} (->> (update-in env [:dependencies] global-exclusions)
                  (pedantic/dep-conflicts)
                  (remove (comp non-transitive? first))))))

(deftask with-cp
  "Specify Boot's classpath in a file instead of as Maven coordinates.

  The --file option is required -- this specifies the PATH of the file that
  will contain the JAR paths as a string suitable for passing to java -cp.

  The default behavior if the --write flag is not specified is to read the
  file specified with --file and load all the JARs onto the classpath. If the
  --write flag is given the --dependencies (or the default depenedncies from
  the boot env, e.g. (get-env :dependencies), if --dependencies isn't provided)
  are resolved and the resulting list of JAR paths are written to the file in
  a format suitable for passing to java -cp.

  The --safe flag configures the task to throw an exception when writing the
  classpath file if there are any unresolved dependency conflicts. These
  conflicts can be resolved by adding :exclusions and by overriding transitive
  dependencies with direct dependencies.

  The --local-repo option specifies the PATH where the dependency JARs are
  stashed. The default if this option is not given is to use the Maven local
  repository setting from the boot environment. This option only applies in
  combination with the --write option.

  The --scopes option can be used to specify which dependency scopes to include
  in the classpath file. The default scopes are compile, runtime, and provided."

  [s safe               bool    "Throw an exception if there are unresolved dependency conflicts."
   w write              bool    "Resolve dependencies and write the classpath file."
   d dependencies EDN   edn     "The (optional) Maven dependencies to resolve and write to the classpath file."
   e exclusions SYM     [sym]   "The vector of Maven group/artifact ids to globally exclude."
   f file PATH          str     "The file containing JARs in java -cp format."
   l local-repo PATH    str     "The (optional) project directory in which to stash resolved JARs."
   S scopes SCOPE       #{str}  "The set of dependency scopes to include (default compile, runtime, provided)."]

  (with-pass-thru [_]
    (let [scopes    (or scopes #{"compile" "runtime" "provided"})
          scope?    #(scopes (:scope (util/dep-as-map %)))
          not-me?   #(not= my-id (first %))
          include?  #(and (scope? %) (not-me? %))
          env-opts  (select-keys *opts* [:local-repo :exclusions :dependencies])]
      (if-not file
        (warn "Expected --file option. Skipping cp task.\n")
        (if-not write
          (doseq [path (string/split (slurp file) #":")]
            (pod/add-classpath path))
          (let [env  (-> (merge-with #(or %2 %1) pod/env env-opts)
                         (update-in [:dependencies] (partial filter include?)))]
            (if-let [conflicts (and safe (not-empty (dep-conflicts env)))]
              (throw (ex-info "Unresolved dependency conflicts." {:conflicts conflicts}))
              (let [resolved        (pod/resolve-dependency-jars env)
                    relative-paths  (map (partial relativize local-repo) resolved)]
                (spit file (apply str (interpose ":" relative-paths)))))))))))
