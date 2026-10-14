# Preguntas de diseño – MiniBank

## 1. Problema de acoplamiento

`TransferService` armaba `Transfer` y `Movement` con constructores de 13 y 7 parámetros, por lo que el servicio dependía de cómo se construye cada objeto del dominio.
Si cambiaba un constructor o se agregaba una regla había que tocar el servicio

```java
// Antes, en TransferService
Transfer transfer = new Transfer(null, cmd.amount(), source.getCurrency(), "COMPLETED", null,
        now, now, ..., cmd.sourceAccountId(), destinationId);   // 13 parámetros
```

---

## 2. Uso de factory

### Qué se hizo

Se agregaron métodos de fábrica en el dominio:

```java
Transfer.create(source, destination, amount, cci, bank, holder, now);
Movement.debit(source, amount, transferId, now);
Movement.credit(destination, amount, transferId, now);
```

Y el servicio quedó así:

```java
Transfer saved = transferRepositoryPort.save(Transfer.create(
        source, destination.orElse(null), cmd.amount(),
        cmd.destinationCci(), cmd.destinationBank(), cmd.destinationHolder(), now));

movementRepositoryPort.save(Movement.debit(source, cmd.amount(), saved.getId(), now));
```

### Por qué Factory

- **Resuelve el problema de la pregunta 1:** el servicio ya no conoce los constructores.
- **Menos errores:** ya no se pueden intercambiar parámetros del mismo tipo por accidente.

### Por qué no Strategy

Strategy sirve cuando hay **varias formas de hacer lo mismo** y cada una tiene lógica propia. Aquí podría aplicarse a transferencias internas y externas, pero hoy la diferencia es solo un `if`:

- Interna: se acredita la cuenta destino.
- Externa: no se acredita nada.

Crear una interfaz y dos clases para un `if` agrega complejidad sin beneficio.
**Strategy tendrá sentido** cuando las transferencias externas tengan lógica propia (comisión, estado `PENDING`, envío a otro banco)

---

## 3. Módulo `auth`
