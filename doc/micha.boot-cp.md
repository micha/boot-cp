# micha.boot-cp

Classpath task for the Boot build tool.

[`my-id`](#my-id)

##### Tasks

 [`with-cp`](#with-cp)

##### Helper Functions

 [`make-pod-cp`](#make-pod-cp)

<hr>

### [`make-pod-cp`](../../1.0.0/src/micha/boot_cp.clj#L29)

```clojure
(make-pod-cp classpath & {:keys [name data], :or {name "pod-cp"}})
```

```
Returns a new pod with the given classpath. Classpath may be a collection
of String or java.io.File objects.

The :name option sets the name of the pod.

The :data option sets the boot.pod/data object in the pod. The data object
is used to coordinate different pods, for example the data object could be
a BlockingQueue or ConcurrentHashMap shared with other pods.

NB: The classpath must include Clojure (either clojure.jar or directories),
but must not include Boot's pod.jar, Shimdandy's impl, or Dynapath. These
are needed to bootstrap the pod, have no transitive dependencies, and are
added automatically.
```

<hr>

### [`my-id`](../../1.0.0/src/micha/boot_cp.clj#L13)

```
FIXME: document this
```

<hr>

### [`with-cp`](../../1.0.0/src/micha/boot_cp.clj#L50)

```clojure
(with-cp & {:as *opts*, :keys [help safe write dependencies exclusions file local-repo scopes]})
```

```
Specify Boot's classpath in a file instead of as Maven coordinates.

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
in the classpath file. The default scopes are compile, runtime, and provided.

Keyword Args:
  :help          bool    Print this help info.
  :safe          bool    Throw an exception if there are unresolved dependency conflicts.
  :write         bool    Resolve dependencies and write the classpath file.
  :dependencies  edn     The (optional) Maven dependencies to resolve and write to the classpath file.
  :exclusions    [sym]   The vector of Maven group/artifact ids to globally exclude.
  :file          str     The file containing JARs in java -cp format.
  :local-repo    str     The (optional) project directory in which to stash resolved JARs.
  :scopes        #{str}  The set of dependency scopes to include (default compile, runtime, provided).
```

<hr>

