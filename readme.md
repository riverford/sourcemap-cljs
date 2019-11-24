# Instructions

This is a very specific tool designed to take two source maps and map a position in react native bundled js back to cljs source code. It helped us find a specific production bug at a specific time, when using the sentry error tracker - your mileage may vary...

## To build the js script
`shadow-cljs release script`

## To run the js script
`node sourcemap.js <options>`

# Options

## Required:

  * `--js` sourcemap to advance compiled js <path/to/js.map>
  * `--cljs` sourcemap to clojurescript <path/to/cljs.map>

## Optional:
  * `--locations` comma separated list of locations e.g. 11:90000, 11: 20000
  * `--stacktrace` stacktrace copied from sentry <path/to/stacktrace.txt>
  * `--match` filter only sources containing text e.g. some-ns

# Basic use
Where `main.ios.map` is the sourcemap that maps from the final bundled js to the advanced compiled js,
and `index.js.map` is the sourcemap that maps from the advanced compiled js to the original cljs.

You can get `main.ios.map` from e.g. expo's post publish hook (see using expo with sentry in the expo docs), and `index.js.map` will be output by shadow-cljs into the target directory alongside index.js, so long as `:compiler-options {:sourcemap true}` is set in `shadow-cljs.edn`.

1. Download main.ios.map and index.js.map from e.g. sentry
2. Run `node sourcemap.js --js ~/Downloads/main.ios.map --cljs ~/Downloads/index.js.map --locations 11:90000`

# Typical process (with stacktracte)

Follow step 1 as above, but then copy the stacktrace string into a file.
Stacktrace will be parsed for <line>:<column> pairs.

```
app:///main.ios.bundle at line 11:935705
app:///main.ios.bundle in Mf at line 11:28521
app:///main.ios.bundle in D at line 11:31231
app:///main.ios.bundle in e at line 11:61222
app:///main.ios.bundle in u at line 11:61158
app:///main.ios.bundle in vw at line 11:149747
...

```
1. `pbpaste > stacktrace.txt`
2. `node sourcemap.js --js ~/Downloads/main.ios.map --cljs ~/Downloads/index.js.map --stacktrace stacktrace.tx

Which should give an output like the following - which is not pretty, but hopefully enough to avoid despair:

```
11:935705
{:source riverford/mobile_app_orders/util.cljs, :line 222, :column 80, :name s}
11:28521
{:source cljs/core.cljs, :line 3901, :column 18, :name a0}
11:31231
{:source cljs/core.cljs, :line 3931, :column 29, :name args}
11:61222
{:source cljs/core.cljs, :line 10789, :column 29, :name args}
11:61158
{:source cljs/core.cljs, :line 10786, :column 11, :name args}
11:149747
{:source riverford/mobile_app_orders/util.cljs, :line 599, :column 56, :name t}
11:420016
{:source riverford/mobile_app_orders/subs/your_account.cljs, :line 74, :column 76, :name me}
```
