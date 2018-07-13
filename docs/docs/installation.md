---
title: Installation
layout: docs
category: docs
order: 2
---

# Installing Figwheel

<div class="lead-in">
Figwheel is just a Clojure library and can be used with many different
tools. This page will show how to include Figwheel as a dependency
with both <a href="">Leiningen</a> and <a href="">Clojure CLI tools</a>.
</div>

<div class="lead-in"> There are two parts to installing Figwheel:
First, you will <strong>install your tools</strong> to work with
Clojure. Second, you will <strong>create a project</strong> that
explicitly requires Figwheel.</div>

## Choosing Leiningen vs. CLI Tools

Let's address the question of whether one should choose
[Leiningen][lein] or [Clojure Tools][cli-tools] to work with
Figwheel. This is largely a matter of taste at this point as these
tools are quite different.

As of the writing of this document [Leiningen][lein] is still the
dominant Clojure dependency/task tool in the Clojure Ecosystem. Most
of the tutorials you find online will be using Leiningen. It has been
around for quite a while and there are innumerable plugins to assist
you with your workflow. It is very stable and it works very well. That
being said it Leiningen takes a *batteries included* view on tooling,
it has a lot of features, and it runs through a bunch of logic to
prepare a running Clojure environment for you. This complexity
requires more investment to understand how and why Leiningen is doing
what it is doing.

[Clojure CLI Tools][cli-tools] takes the opposite approach. It is
minimal and it requires you to add functionality with different
libraries as you need it. This simplicity gives [CLI Tools][cli-tools]
the immediate advantage of a fast start up time. This simplicity is
also means that you can be fairly certain how the Java Environment is
created when you run something with [CLI Tools][cli-tools], which
makes environmental problems easier to understand. The downside of
this, is that features that are built into [Leiningen][lein] need to
be added manually as libraries, if they are even available at all. As
it is still early for [CLI Tools][cli-tools] some of these libraries
are quite new and not as battle tested.

At the end of the day Figwheel will work absolutely fine with either
of these tools, and it is quite likely that you will use it with
both of these tools even in one project.

This document is going to prefer [CLI Tools][cli-tools] because:

* it starts quickly
* it has less configuration options
* it's very capable doing anything you need to do with `figwheel.main`
* does not obscure `figwheel.main`'s usage

**On Windows**

As of this writing [CLI Tools][cli-tools] is not available on the
Windows Operating System yet. For now, if you are on Windows you will
need to use [Leiningen][lein].

## Install your tool of choice

You will need to install the latest version of [Leiningen][lein] or
[CLI Tools][cli-tools].

**Install Leiningen**

Make sure you have the latest version of [Leiningen][install-lein].

You can check that everything has been installed correctly by running
a Clojure REPL. In your terminal application at the shell prompt enter
the following:

```shell
$ lein repl
```

You should see a `user=>` prompt where you can enter Clojure code.
Type `Control-D` to quit out of the Clojure REPL.

**Install CLI Tools**

First we will want to [install][cli-tools] the `clj` and `clojure` [command line
tools][cli-tools].

If you are on Mac OSX and you can quickly install the Clojure tools
via [homebrew](brew).

In the terminal at the shell prompt enter:

```shell
$ brew install clojure
```

If you've already installed Clojure, now is a great time to ensure
that you have the latest version installed with:

```shell
$ brew upgrade clojure
```

You can check that everything has been installed correctly by running
a Clojure REPL. In your terminal application at the shell prompt enter
the following:

```shell
$ clj
```

You should see a `user=>` prompt where you can enter Clojure code.
Type `Control-C` to quit out of the Clojure REPL.


## Adding Figwheel as a dependency

You can add `com.bhauman/figwheel-main` by itself as a dependency to
get started. However, you are better off adding the latest version of
ClojureScript along with Rebel Readline.

Adding `org.clojure/clojurescript` as a dependency will allow to
ensure that you are getting the latest version of ClojureScript not
just the base version that Figwheel will work with.

Adding a `com.bhauman/rebel-readline-clj` dependency will make the
terminal REPL Figwheel launches much more capable. It is optional but
highly recommended.

You will likely need to add other dependencies like
[Sablono][sablono], [Reagent][reagent] or
[Re-frame][re-frame]. However, these are not needed to work with
Figwheel.

> Many explanations in this document assume that you are currently in
> the root directory of a **project**, which is just a directory that
> contains all the code and other file assets for the program you are
> working on.

### with Leiningen

Leiningen requires `project.clj` file in the root of your project. In
your `project.clj` file you will need to add
`com.bhauman/figwheel-main` as a dependency.

As a concrete example, in the root directory of your project place a
`project.clj` that at least contains the following configuration:

```clojure
(defproject example-project "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :profiles 
    {:dev 
      {:dependencies [[org.clojure/clojurescript "1.10.339"]
                      [com.bhauman/figwheel-main "0.1.4"]
                      ;; optional but recommended
                      [com.bhauman/rebel-readline-cljs "0.1.4"]]}})
```

We added all the dependencies to work with ClojureScript and Figwheel
into the `:dev` profile which is enabled by default when you are
working with Leiningen, these dependencies won't be included when you
create an artifact for deployment like a jar or an uberjar.

You can verify this worked by launching a generic `figwheel.main`
ClojureScript REPL.

```shell
$ lein trampoline run -m figwheel.main
```

> When using Leiningen with Rebel Readline you will have to use
> **trampoline**.

A browser window should pop open and back in the terminal you should
see a REPL with a `cljs.user=>` prompt waiting to evaluate
ClojureScript code.

You can quit the REPL with `Control-C Control-D`.

### with CLI Tools

In order to work with Clojure CLI tools you will need a `deps.edn`
file in the root directory of your project.

As an example, in the root directory place a `deps.edn` file with the
following contents:

```clojure
{:deps {org.clojure/clojure {:mvn/version "0.1.9"}
        com.bhauman/figwheel-main {:mvn/version "0.1.4"}
        ;; optional but recommended		
        com.bhauman/rebel-readline-cljs {:mvn/version "0.1.4"}}
```

You can verify this worked by launching a generic `figwheel.main`
ClojureScript REPL.

```shell
$ clojure -m figwheel.main
```

A browser window should pop open and back in the terminal you should
see a REPL with a `cljs.user=>` prompt waiting to evaluate
ClojureScript code.

You can quit the REPL with `Control-C Control-D`.

## Aliases












[clojurescript]: https://clojurescript.org
[cli-tools]: https://clojure.org/guides/deps_and_cli
[lein]: https://leiningen.org/
[install-lein]: https://github.com/technomancy/leiningen#installation
[re-frame]: https://github.com/Day8/re-frame
[reagent]: http://reagent-project.github.io
[sablono]: https://github.com/r0man/sablono