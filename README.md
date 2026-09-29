# MIVAR

MIVAR — экспериментальный язык, в котором программа задаётся переменными и
правилами-гиперрёбрами. Правило активируется только когда известны все его
входы, а затем за один шаг выдаёт все выходы. Планировщик делает BFS по
множествам известных переменных и находит план с минимальным числом правил.

Ядро следует object-oriented стилю: `MivarObject`, `Rule` и `Expr` —
интерфейсы контрактов. Базовые реализации (`BasicObject`, `BasicRule`) могут
оборачиваться декораторами (`ObjectDecorator`, `RuleDecorator`,
`CheckedRule`, `ExprDecorator`, `TracingExpr`) без изменения планировщика.
Это оставляет место для memoization, tracing, type checking и security policy
как независимых объектов.

## Объекты, математика и логика

Всё существенное представляется объектами и атрибутами. Правило может иметь
условие и несколько вычисляемых выходов:

```mivar
object input {
  a = 10
  b = 20
  enabled = true
}

rule sum {
  when input.enabled
  emit x = input.a + input.b
  emit y = x * 2
}

rule valid {
  when x > 20 && y == 60
  emit result = true
}
```

Поддерживаются `+ - * / %`, сравнения `== != < <= > >=`, логические
операторы `&& || !` и скобки.

## Компактный синтаксис

```mivar
object a
object b
object c
object x
object y
object z

rule rule1: a, b, c -> x, y
rule rule2: a, b -> z
rule rule3: x, y -> z
```

Комментарии начинаются с `#`. Имена чувствительны к регистру.

## Запуск

```bash
mvn test
mvn package -DskipTests
java -cp target/classes org.example.Main example.mivar a,b,c z
```

Анализатор проверяет дубли правил, неизвестные переменные и недостижимые
цели. Планировщик использует индекс правил по входным переменным, поэтому не
перебирает все правила на каждом шаге BFS.
