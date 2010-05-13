* Prolefeed

Prolefeed is a simple Clojure library for fetching RSS/Atom feeds and converting them
to Clojure data structures.

It provides both simple feed fetching, and a scheduled background fetcher for situations
where you want to stay up-to-date without needing to incur the overhead of an HTTP request
each time you need to get the contents of a feed.

Prolefeed wraps the [ROME](https://rome.dev.java.net/) library, which does all the heavy lifting
related to feed fetching and parsing. It also uses the [RomeFetcher](http://wiki.java.net/bin/view/Javawsxml/RomeFetcher)
library, which employs HTTP conditional GETs and GZip encoded feeds where possible. This minimizes the
HTTP traffic required to fetch feeds.

** Installation

The easiest way to install Prolefeed is using [Leiningen](http://github.com/technomancy/leiningen).
Add the following to your dependencies in project.clj:

    [prolefeed "0.1-SNAPSHOT"]

** Usage

To simply fetch a feed from a URL, call the `fetch` function:

    (ns my-app
      (:require prolefeed))

    (defn fetch-clojure-reddit []
      (prolefeed/fetch "http://www.reddit.com/r/clojure.rss"))

To schedule a regular retrieval of a feed, call `add-feed`. It returns a function
which will always immediately return the latest fetched feed content, without
causing an HTTP request:

    (ns my-app
      (:require prolefeed))

    (let [clojure-reddit (prolefeed/add-feed "http://www.reddit.com/r/clojure.rss")]
      (defn fetch-clojure-reddit []
        (clojure-reddit)))

By default the feed will be fetched every 5 minutes. You can also specify your own
interval (in seconds), by giving it as a second argument to `add-feed`:

    (prolefeed/add-feed "http://www.reddit.com/r/clojure.rss" 60)

The data structures returned will have the following form:

    {:author "..."
     :copyright "..."
     :description "..."
     :language "..."
     :link "..."
     :publishedDate "..."
     :title "..."
     :uri "..."
     :entries ({:author "..."
                :link "..."
                :publishedDate "..."
                :title "..."
                :updatedDate "..."
                :uri "..."
                :description "..."
                :contents ("...", "...")})}

** License

Copyright (C) 2010 Tero Parviainen

Distributed under the Eclipse Public License.
