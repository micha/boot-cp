# boot-cp

[](dependency)
```clojure
[org.clojars.micha/boot-cp "0.1.4"] ;; latest release
```
[](/dependency)

Classpath task for boot.

## Demo

```shell
# see task docs
boot -d org.clojars.micha/boot-cp with-cp --help
```
```shell
# write a classpath file
boot -d org.clojars.micha/boot-cp -d ring:1.4.0 with-cp -l lib -wf cp
```
```shell
# start a REPL with the claspath set up by the file
boot -d org.clojars.micha/boot-cp with-cp -f cp repl
```

## Usage

```clojure
;; build.boot

(set-env!
  :dependencies '[[org.clojars.micha/boot-cp "X.Y.Z" :scope "test"]])

(require
  '[micha.boot-cp :refer [with-cp]])

(task-options!
  with-cp {:pedantic      true
           :file          "deps.out"
           :scopes        #{"compile"}
           :dependencies  '[[foo/bar "1.2.3"]
                            [baz/baf "4.5.6"]]})
```

To write the classpath file `cp` do:

```
boot with-cp -w
```

This will create a file named `cp` with a `java -cp` compatible string of paths
to dependency JARs.

To load dependencies from a previously created classpath file do:

```
boot with-cp task1 task2 ...
```

## Hacking on boot-cp

```shell
boot build-jar # build JAR and install to local Maven repo
```
```shell
boot build-jar push-release # build JAR, install, and push to Clojars
```
