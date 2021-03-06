(ns clj-kondo.unused-import-test
  (:require
   [clj-kondo.test-utils :refer [lint! assert-submaps]]
   [clojure.test :refer [deftest testing is]]))

(deftest unused-import-test
  (testing "Detecting unused imports"
    (assert-submaps
     '({:file "<stdin>",
        :row 1,
        :col 21,
        :level :warning,
        :message "Unused import Foo"}
       {:file "<stdin>",
        :row 1,
        :col 25,
        :level :warning,
        :message "Unused import Bar"})
     (lint! "(import '[java.util Foo Bar])"))
    (assert-submaps
     '({:file "<stdin>",
        :row 1,
        :col 29,
        :level :warning,
        :message "Unused import Foo"}
       {:file "<stdin>",
        :row 1,
        :col 33,
        :level :warning,
        :message "Unused import Bar"})
     (lint! "(ns foo (:import [java.util Foo Bar]))"))
    (assert-submaps
     '({:file "<stdin>",
        :row 1,
        :col 10,
        :level :warning,
        :message "Unused import Foo"})
     (lint! "(import 'java.util.Foo)"))
    (assert-submaps
     '({:file "<stdin>",
        :row 1,
        :col 10,
        :level :warning,
        :message "Unused import Long"}
       {:file "<stdin>",
        :row 1,
        :col 37,
        :level :warning,
        :message "Unused import Vec2"}
       {:file "<stdin>",
        :row 1,
        :col 42,
        :level :warning,
        :message "Unused import Vec3"}
       {:file "<stdin>",
        :row 1,
        :col 59,
        :level :warning,
        :message "Unused import Integer"})
     (lint! "(import 'goog.math.Long '[goog.math Vec2 Vec3] [goog.math Integer])")))
  (testing "Preventing false positives"
    (is (empty? (lint! "(import '[java.util Foo Bar]) Foo Bar")))
    (is (empty? (lint! "(import '[java.util Foo]) (Foo.)")))
    (is (empty? (lint! "(ns cheshire.test.custom (:import (java.sql Timestamp))) (Timestamp.)")))
    (is (empty? (lint! "(ns cheshire.test.custom (:import (java.sql Timestamp))) `(Timestamp.)")))
    (is (empty? (lint! "(ns bar (:import [java.util Foo Bar])) Foo Bar")))
    (is (empty? (lint! "(import '[java.util Foo Bar]) Foo/CONSTANT (Bar/static_fn)")))
    (is (empty? (lint! "(import '[java.util Foo Bar]) Foo/CONSTANT (Bar/static_fn)"
                       "--lang" "cljs")))
    (is (empty? (lint! "(import '[java.util Foo]) (defn foo [^Foo x] x)")))
    (is (empty? (lint! "(import '[java.util Foo]) (try 1 (catch Foo _e nil))")))))
