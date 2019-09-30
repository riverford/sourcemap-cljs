# Instructions

`shadow-cljs watch script`

`node sourcemap.js <options>`

# Options

## Required:

  --js sourcemap to advance compiled js <path/to/js.map>
  --cljs sourcemap to clojurescript <path/to/cljs.map>

## Optional:
  --locations comma separated list of locations e.g. 11:90000, 11: 20000
  --stacktrace stacktrace copied from sentry <path/to/stacktrace.txt>
  --match filter only sources containing text e.g. some-ns
