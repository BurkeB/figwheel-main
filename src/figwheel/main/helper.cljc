(ns figwheel.main.helper
  (:require
   #?@(:cljs
       [[goog.dom :as gdom]
        [goog.dom.classlist :as classlist]
        [goog.net.XhrIo :as xhr]
        [goog.object :as gobj]]
       :clj [[clojure.java.io :as io]
             [figwheel.server.ring :refer [best-guess-script-path]]])
   [clojure.string :as string])
  #?(:cljs
     (:import
      [goog Promise])))

#?(:cljs
   (do

     (def sidebar-link-class "figwheel_main_content_link")
     (def sidebar-focus-class "figwheel_main_content_link_focus")

     (defn connected-signal []
       (some-> (gdom/getElement "connect-status")
               (classlist/add "connected")))

     (defn on-disconnect []
       (some-> (gdom/getElement "connect-status")
               (classlist/remove "connected")))

     (defonce resource-atom (atom {}))

     (defn get-content [url]
       (Promise.
        (fn [succ e]
          (if-let [res (get @resource-atom url)]
            (succ res)
            (xhr/send url
                      (fn [resp]
                        (some-> resp
                                (gobj/get "currentTarget")
                                (.getResponseText)
                                (#(do (swap! resource-atom assoc url %)
                                      %))
                                succ)))))))

     (defn load-content [url div-id]
       (.then (get-content url)
              (fn [content]
                (gdom/getElement div-id)
                (set! (.-innerHTML (gdom/getElement div-id))
                      content))))

     (defn focus-anchor! [anchor-tag]
       (.forEach (gdom/getElementsByTagNameAndClass "a" sidebar-link-class)
                 (fn [a]
                   (classlist/remove a sidebar-focus-class)
                   (when (= a anchor-tag)
                     (classlist/add a sidebar-focus-class)))))

     (defn init-sidebar-link-actions! []
       (.forEach (gdom/getElementsByTagNameAndClass "a" sidebar-link-class)
                 (fn [a]
              ;; pre-fetch
                   (when-let [content-loc (.-rel a)]
                     (get-content content-loc)
                     (.addEventListener
                      a "click"
                      (fn [e]
                        (.preventDefault e)
                        (focus-anchor! a)
                        (when-let [content-loc (.-rel (.-target e))]
                          (load-content content-loc "main-content"))))))))

     (defn on-connect [e]
       (init-sidebar-link-actions!)
       (connected-signal))

     (defonce initialize
       (do
         (.addEventListener js/document.body "figwheel.repl.connected"
                            on-connect)
         (.addEventListener js/document.body "figwheel.repl.disconnected"
                            on-disconnect)))))

#?(:clj
   (do
     (defn main-wrapper [{:keys [body output-to header app-div-class sidebar]
                          :or {app-div-class "app"
                               sidebar true} :as options}]
       (format
        "<!DOCTYPE html>
<html>
  <head>
   <meta charset=\"UTF-8\">
   <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
  </head>
  <body>
    <div id=\"%s\">
      <link href=\"/com/bhauman/figwheel/helper/css/style.css\" rel=\"stylesheet\" type=\"text/css\">
      <link href=\"/com/bhauman/figwheel/helper/css/coderay.css\" rel=\"stylesheet\" type=\"text/css\">
      <header>
        <div class=\"container\">
          <div id=\"connect-status\">
            <img class=\"logo\" width=\"60\" height=\"60\" src=\"/com/bhauman/figwheel/helper/imgs/cljs-logo-120b.png\">
            <div class=\"connect-text\"><span id=\"connect-msg\"></span></div>
          </div>
          <div class=\"logo-text\">%s</div>
        </div>
        <div class=\"container top-level-nav\">
             <a href=\"http://figwheel.org\" target=\"_blank\">Figwheel Main</a>
             <a href=\"https://clojurescript.org\" target=\"_blank\">CLJS</a>
             <a href=\"https://cljs.info\" target=\"_blank\">Cheatsheet</a>
             <a href=\"https://kanaka.github.io/clojurescript/web/synonym.html\" target=\"_blank\">Synonyms</a>
        </div>
      </header>

      <div class=\"container\">
        %s
        <section id=\"main-content\">
        %s
        </section>
      </div>
      <footer>
        <div class=\"container flex-column\">
          <div class=\"off-site-resources\">
             <h6>Figwheel Main</h6>
             <a href=\"https://figwheel.org\" target=\"_blank\">Figwheel Main Home / Readme</a>
             <a href=\"https://figwheel.org/docs\" target=\"_blank\">Documentation</a>
             <a href=\"https://figwheel.org/config-options\" target=\"_blank\">Config Options</a>
             <a href=\"https://github.com/bhauman/figwheel-main\" target=\"_blank\">Github</a>
          </div>
          <div class=\"off-site-resources\">
             <h6>Clojurescript</h6>
             <a href=\"https://clojurescript.org\" target=\"_blank\">ClojureScript Home</a>
             <a href=\"https://cljs.info\" target=\"_blank\">API Cheatsheet</a>
             <a href=\"https://kanaka.github.io/clojurescript/web/synonym.html\" target=\"_blank\">JavaScript Synonyms</a>
          </div>
          <div class=\"off-site-resources\">
             <h6>Community</h6>
             <a href=\"http://clojurians.slack.com\" target=\"_blank\">#clojurescript on Slack</a>
             <a href=\"http://clojurians.slack.com\" target=\"_blank\">#figwheel-main on Slack</a>
             <a href=\"http://clojurians.net\" target=\"_blank\">get Clojurians Slack invite</a>
             <a href=\"http://groups.google.com/group/clojurescript\" target=\"_blank\">ClojureScript Google Group</a>
             <a href=\"https://clojureverse.org/\" target=\"_blank\">ClojureVerse</a>
          </div>
        </div>
      </footer>
    %s
    </div> <!-- end of app div -->
    <script type=\"text/javascript\">%s</script>
  </body>
</html>"
        (str app-div-class)
        (str header)
        (cond
          (true? sidebar)
          "<aside>
          <a class=\"figwheel_main_content_link\" href=\"javascript:\" rel=\"/figwheel/helper/welcome\">Welcome</a>
          <a class=\"figwheel_main_content_link\" href=\"javascript:\" rel=\"/com/bhauman/figwheel/helper/content/creating_a_build_cli_tools.html\">Create a build</a>
          <a class=\"figwheel_main_content_link\" href=\"javascript:\" rel=\"/com/bhauman/figwheel/helper/content/creating_a_build_lein.html\">Create a build (lein)</a>
          <a class=\"figwheel_main_content_link\" href=\"javascript:\" rel=\"/com/bhauman/figwheel/helper/content/css_reloading.html\">Live Reload CSS</a>
        </aside>"
          (string? sidebar)
          sidebar
          :else "")
        (str body)
        (str
         (when-not (:dev-mode options)
           "<script src=\"/com/bhauman/figwheel/helper.js\"></script>"))
        (str
         (when (and output-to
                    (.isFile (io/file output-to)))
           (-> (slurp output-to)
               (string/replace #"<\/script" "<\\\\/script"))))))

     (defn main-action [req options]
       {:status 200
        :headers {"Content-Type" "text/html"}
        :body (main-wrapper options)})

     (defn dev-endpoint [req]
       (let [method-uri ((juxt :request-method :uri) req)]
         (when (= method-uri [:get "/"])
           (main-action req {:output-to "target/public/cljs-out/helper-main.js"
                             :dev-mode true
                             :header "Dev Mode"
                             :body (slurp (io/resource "public/com/bhauman/figwheel/helper/content/repl_welcome.html"))}))))

     (defn middleware [handler options]
       (fn [req]
         (let [method-uri ((juxt :request-method :uri) req)]
           (cond
             (and (= [:get "/figwheel/helper/welcome"] method-uri) (:body options))
             {:status 200
              :headers {"Content-Type" "text/html"}
              :body (:body options)}
             (= method-uri [:get "/"])
             (main-action req options)
             :else (handler req)))))

     (defn missing-index-middleware [handler options]
       (fn [r]
         (let [method-uri ((juxt :request-method :uri) r)]
           (cond
             (and (= [:get "/figwheel/helper/welcome"] method-uri)
                  (:body options))
             {:status 200
              :headers {"Content-Type" "text/html"}
              :body (:body options)}
             :else
             (let [res (handler r)]
               (if (and (= [:get "/"] method-uri)
                        (= 404 (:status res)))
                 (main-action r options)
                 res))))))

     (defn default-index-code [output-to]
       (let [path (best-guess-script-path output-to)]
         (str
          "<div class=\"CodeRay\">"
          "<div class=\"code\">"
          "<pre>"
          "&lt;!DOCTYPE html&gt;\n"
          "&lt;html&gt;\n"
          "  &lt;head&gt;\n"
          "    &lt;meta charset=\"UTF-8\"&gt;\n"
          "  &lt;/head&gt;\n"
          "  &lt;body&gt;\n"
          "    &lt;div id=\"app\"&gt;\n"
          "    &lt;/div&gt;\n"
          "    &lt;script src=\""
          (if path path
              (str "[correct-path-to "
                   (or output-to "main.js file")
                   "]"))
          "\" type=\"text/javascript\"&gt;&lt;/script&gt;\n"
          "  &lt;/body&gt;\n"
          "&lt;/html&gt;\n"
          "</pre></div></div>")))

     (defn missing-index [handler options]
       (missing-index-middleware
        handler
        (merge
         {:header "Figwheel Default Dev Page"
          :body (str
                 (slurp (io/resource "public/com/bhauman/figwheel/helper/content/missing_index.html"))
                 (default-index-code (:output-to options)))}
         options)))

     (defn extra-main-body [nm output-to]
       (str (format (str "<h1>Host page for <code>:%s</code> main file</h1>"
                         "<blockquote>You can override the content of this "
                         "page by replacing the contents of the <code>app-%s</code> "
                         "div.</blockquote><p></p>"
                         "<blockquote>You can create your own "
                         "<a href=\"https://figwheel.org/docs/your_own_page.html\" target=\"_blank\">host page</a> "
                         "for this extra main by including the "
                         "<code>%s</code> bootstrap script in your host page. "
                         "If you are unsure how to create a host page please see this "
                         "<a href=\"https://figwheel.org/docs/your_own_page.html\" target=\"_blank\">documentation</a>. "
                         "</blockquote>") nm nm (if (string/includes? output-to "/public/")
                                                  (str "/" (second (string/split output-to #"\/public\/")))
                                                  (format "/cljs-out/[build-id]-main-%s.js" nm)))))

     (defn extra-main-hosting [handler
                               name-output-to-map]
       (if (not-empty name-output-to-map)
         (fn [req]
           (let [[method uri] ((juxt :request-method :uri) req)]
             (if (and (= :get method)
                      (string/starts-with? uri "/figwheel-extra-main/"))
               (let [nm (string/replace uri "/figwheel-extra-main/" "")]
                 (if-let [output-to (name-output-to-map nm)]
                   (main-action req {:output-to output-to
                                     :header (format "Extra Main: %s Host Page" (string/capitalize nm))
                                     :app-div-class (str "app-" nm)
                                     :sidebar false
                                     :body (extra-main-body nm output-to)})
                   (handler req)))
               (handler req))))
         handler))

     (defn serve-only-middleware [handler options]
       (missing-index-middleware
        handler
        (merge
         {:header "Server Only Page"
          :body (slurp (io/resource "public/com/bhauman/figwheel/helper/content/server_only_welcome.html"))}
         options))))) ;; clj conditional reader end
