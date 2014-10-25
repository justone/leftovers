FROM clojure
ADD . /code
WORKDIR /code
RUN lein cljsbuild once
