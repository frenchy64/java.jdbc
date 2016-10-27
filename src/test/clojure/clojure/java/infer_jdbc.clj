(ns clojure.java.infer-jdbc
  (:require [clojure.test :refer :all]
            [clojure.core.typed :as t]
            [clojure.core.typed.runtime-infer :as infer]))

(def ^:dynamic *infer-fn* t/runtime-infer)

(defn delete-anns [nss]
  (doseq [ns nss]
    (infer/delete-generated-annotations
      ns
      {:ns ns})))

(defn infer-anns [nss]
  (doseq [ns nss]
    (*infer-fn* :ns ns)))

(def infer-files
  '[clojure.java.jdbc
    ])

(defn infer [spec-or-type]
  (binding [*infer-fn* (case spec-or-type
                         :type t/runtime-infer
                         :spec t/spec-infer)]
    ;; FIXME shouldn't need this, but some types
    ;; don't compile
    (delete-anns infer-files)

    (def tests 
      '[clojure.java.test-jdbc
        clojure.java.test-utilities
        ])

    (apply require tests)
    (apply run-tests tests)

    (infer-anns infer-files)))
