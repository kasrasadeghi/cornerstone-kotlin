(decl putchar (types i32) i32)

(def i (params (c i32)) void
  (call putchar (types i32) i32 (args c))
  (return void))

(def returnsVoid (params) void
  (call i (types i32) void (args 48))
  (call i (types i32) void (args 49))
  (become i (types i32) void (args 12)))

(def main (params) i32
  (call returnsVoid (types) void (args))
  (return 0 i32))