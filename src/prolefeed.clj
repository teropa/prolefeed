(ns prolefeed
  (:import [java.net URL])
  (:import [java.util.concurrent ScheduledThreadPoolExecutor TimeUnit])
  (:import [com.sun.syndication.fetcher.impl HashMapFeedInfoCache HttpURLFeedFetcher]))

(def *thread-pool-size* 2)

(let [feed-fetcher (HttpURLFeedFetcher.
                     (HashMapFeedInfoCache/getInstance))
      scheduler (ScheduledThreadPoolExecutor. *thread-pool-size*)]

  (defn- convert-content [content]
    "Converts an instance of SyndContent to a Clojure data structure (currently just a string containing the value)"
    (if content (.getValue content)))

  (defn- convert-entry [entry]
    "Converts a SyndEntry to a Clojure map"
    (-> (select-keys
          (bean entry)
          [:author :link :publishedDate :title :updatedDate :uri])
        (assoc :description (convert-content (.getDescription entry)))
        (assoc :contents (map convert-content (.getContents entry)))))

  (defn- convert-feed [feed]
    "Converts a SyndFeed to a Clojure map"
    (-> (select-keys
          (bean feed)
          [:author :copyright :description :language :link :publishedDate :title :uri])
        (assoc :entries (map convert-entry (.getEntries feed)))))

  (defn fetch [url]
    "Fetches and immediately returns the current contents of the feed in the given URL."
    (convert-feed
      (.retrieveFeed feed-fetcher (URL. url))))

  (defn add-feed
    "Schedules a retrieval of the feed in the given URL to run every x seconds (default 300).
     Returns a function that immediately returns the latest fetched contents of the feed."
    ([url] (add-feed url 300))
    ([url interval]
      (let [feed (atom (fetch url))]
        (.scheduleWithFixedDelay scheduler
          #(reset! feed (fetch url))
          interval
          interval
          TimeUnit/SECONDS)
        #(deref feed)))))
