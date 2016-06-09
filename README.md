# boot-cp

[](dependency)
```clojure
[org.clojars.micha/boot-cp "0.1.0"] ;; latest release
```
[](/dependency)

Classpath task for boot.

## Usage

```clojure
;; build.boot

(set-env!
  :dependencies '[[org.clojars.micha/boot-cp "X.Y.Z"]])

(require
  '[micha.boot-cp :refer [with-cp]])

(task-options!
  with-cp {:dependencies '[[foo/bar "1.2.3"]
                           [baz/baf "4.5.6"]]})
```

To write the classpath file `cp` do:

```
boot with-cp -o cp
```

This will create a file named `cp` with a `java -cp` compatible string of paths
to dependency JARs.

To load dependencies from a previously created classpath file do:

```
boot with-cp -i cp task1 task2 ...
```

The `-l, --libdir, :libdir` option allows you to specify a directory in which
to stash the dependency JARs. This is optional &mdash; the default behavior is
to use the local Maven repository location.

## Hacking

```shell
boot build-jar # build JAR and install to local Maven repo
```
```shell
boot build-jar push-release # build JAR, install, and push to Clojars
```
