Cornerstone Compiler Infrastructure

+++

Grammar of Texp
```
Texp = (#word (* Texp))

  ~=
  
Texp := word Texp*
```

+++

Simplifies to something similar to
Parser Expression Grammars/Packrat Parsers

+++

Texp in C
```c
typedef struct Texp {
  char* value;
  struct Texp** list;
  size_t len;
  size_t cap;
} Texp;
```

Texp in Haskell
```haskell
type Texp = String [Texp]
```

+++

Texp's are Tries, but with:
 - contiguous memory 
 - pointers

Any data structure <: Trie

Restrict with Grammars

+++

Languages = data structure repr. with a Trie

---

```
(Grammar, Compiler DSL) -> (Language-Texp -> Language-Texp)
```

The Type of a piece of source is Language-Texp.
The Type of a Language-Texp is its Language.

+++

The Grammar is the Type of a Language
