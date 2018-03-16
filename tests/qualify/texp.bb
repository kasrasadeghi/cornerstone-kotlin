(struct Texp
  (value i8*)
  (list Texp**)
  (len u64)
  (cap u64))

(str-table
  (0 "Texp{value: %s, len: %lu}\0A\00")
  (1 "Example Texp"))

(decl malloc (types i64) i8*)
(decl calloc (types i64 i64) i8*)

(def makeTexp (params (value i8*) (length i64)) Texp*
  (let r (cast i8* Texp* (call malloc (types i64) i8* (args 32))))
  (store a i8* (index r Texp 0))

  (if (= u64 length 0)
    (store 0 Texp** (index r Texp 1)))

  (if (!= u64 length 0)
    (store
      (cast i8* Texp** (call calloc (types i64 i64) i8* (args length 32)))
      Texp**
      (index r Texp 1)))

  (store length u64 (index r Texp 2))
  (store length u64 (index r Texp 3))

  (return r Texp*))

(decl printf (types i8* ...) i32)
(decl free (types i8*) void)

(def main (params (argc i32) (argv i8**)) i32
  (let s (call makeTexp (types i8* i64) Texp* (args (str-get 1) 0)))
  (call-vargs printf (types i8* i8* u64) i32 (args
    (str-get 0)
    (load i8* (index s Texp 0))
    (load u64 (index s Texp 2))
  ))
  (call free (types i8*) void (args (cast Texp* i8* s)))
  (return 0 i32))