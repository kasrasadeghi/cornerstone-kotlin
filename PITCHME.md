Cornerstone Compiler Infrastructure

+++

Grammar of Sexp
```
Sexp = (#word (* Sexp))

  ~=
  
Sexp := word Sexp*
```

+++

Simplifies to something similar to
Parser Expression Grammars/Packrat Parsers

+++

Sexp in C
```c
typedef struct Sexp {
  char* value;
  struct Sexp** list;
  size_t len;
  size_t cap;
} Sexp;
```

Sexp in Haskell
```haskell
type Sexp = String [Sexp]
```

+++

Sexp's are Tries, but with:
 - contiguous memory 
 - pointers

Any data structure <: Trie

Restrict with Grammars

+++

Languages = data structure repr. with a Trie

---

(Grammar, Compiler DSL) -> (Language-Sexp -> Language-Sexp)

The Type of a piece of source is Language-Sexp.
The Type of a Language-Sexp is its Language.

+++

The Grammar is the Type of a Language
