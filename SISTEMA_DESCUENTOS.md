# Sistema de Cupones y Reglas de Descuento - Ada Commerce

## Descripción General

Se ha implementado un sistema completo de cupones de descuento y reglas de descuento para el e-commerce Ada Commerce. El sistema permite crear, gestionar y aplicar diferentes tipos de descuentos a los pedidos.

## Funcionalidades Implementadas

### 1. Cupones de Descuento

#### Características:
- **Código único**: Cada cupón tiene un código identificador único
- **Tipos de descuento**: 
  - `FIXED_AMOUNT`: Descuento fijo en valor monetario
  - `PERCENTAGE`: Descuento porcentual
  - `PROGRESSIVE`: Descuento que aumenta con el aumento de cantidades del pedido 
- **Validación temporal**: Fechas de inicio y expiración
- **Control de uso**: Límite máximo de usos y seguimiento de usos actuales
- **Valor mínimo**: Establece un valor mínimo del pedido para aplicar el cupón
- **Estado**: Activo/Inactivo, Usado/No usado

#### Operaciones disponibles:
- Crear cupones de descuento
- Listar todos los cupones
- Listar cupones disponibles (válidos y no usados)
- Actualizar cupones existentes
- Expirar cupones vencidos automáticamente
- Aplicar cupones a pedidos
- Remover cupones de pedidos

### 2. Reglas de Descuento

#### Tipos de Reglas:

##### Reglas Simples:
- Descuento directo con valor fijo o porcentual
- Aplicación basada en valor mínimo del pedido
- Validación temporal

##### Reglas Compuestas:
- Descuento progresivo basado en la cantidad de items
- Operadores lógicos:
  - `AND`: Aplica todas las reglas y suma los descuentos
  - `OR`: Aplica la regla con mayor descuento
- Jerarquía de reglas padre-hijo


### 3. Integración con Pedidos

#### Nuevas funcionalidades en pedidos:
- **Subtotal**: Suma de todos los items sin descuentos
- **Descuento por cupón**: Descuento aplicado por cupón
- **Descuento por regla**: Descuento aplicado por regla de descuento
- **Total**: Subtotal menos todos los descuentos aplicados
- **Aplicación de cupones**: Solo en pedidos abiertos

## Estructura del Código

### Entidades de Dominio:
- `Coupon`: Entidad principal de cupones
- `DiscountType`: Enum para tipos de descuento (FIXED_AMOUNT, PERCENTAGE)
- `DiscountRule`: Entidad para reglas de descuento
- `RuleType`: Enum para tipos de regla (SIMPLE, COMPOUND)
- `RuleOperator`: Enum para operadores lógicos (AND, OR)

### Servicios:
- `CouponService`: Gestión completa de cupones
- `DiscountRuleService`: Gestión completa de reglas de descuento
- `OrderService`: Actualizado para soportar cupones y reglas

### Clases de Gestión:
- `CouponManagement`: Interfaz de usuario para gestión de cupones
- `DiscountRuleManagement`: Interfaz de usuario para gestión de reglas

## Uso del Sistema

### Menú Principal Actualizado:
```
 ======MEMU E-COMMERCE======
 1-  Cadastrar Cliente
 2-  Listar Clientes
 3-  Cadastar Produto
 4-  listar Produtos
 5-  Criar Pedido
 6-  Adicionar Item ao Pedido
 7-  Remover Item do Pedido
 8-  Alterar quantidade do Item do pedido
 9-  Finalizar Pedido
 10- Pagar Pedido
 11- Entregar Pedido
 12- Listar Pedidos
 13- Aplicar Cupón de Descuento
 14- Remover Cupón de Descuento
 15- Criar Cupons de descuento
 16- Listar Cupones Disponibles
 17- Atualizar Cupón de Descuento
  0- Sair
  ==== Escolha uma opcao: ====
```

### Flujo de Trabajo Típico:

1. **Crear un cupón**:
   - Ir a opción 15 
   - Seleccionar "Crear Cupón"
   - Ingresar código, descripción, valor, tipo, fechas, etc.

2**Aplicar descuentos a un pedido**:
   - Crear un pedido (opción 5)
   - Agregar items al pedido (opción 6)
   - Aplicar cupón (opción 13) 
   - Finalizar pedido (opción 9)

## Validaciones Implementadas

### Cupones:
- Código único obligatorio
- Valor de descuento mayor que cero
- Fechas válidas (inicio antes de expiración)
- Máximo de usos mayor que cero
- Solo se pueden aplicar a pedidos abiertos
- Verificación de validez antes de aplicar

### Reglas:
- Nombre obligatorio
- Valor de descuento mayor que cero
- Fechas válidas
- Solo reglas simples pueden ser hijas
- Solo reglas compuestas pueden tener operadores

### Pedidos:
- Solo pedidos abiertos pueden recibir cupones
- Verificación de valor mínimo antes de aplicar descuentos
- Cálculo correcto del total con descuentos

## Ejemplos de Uso

### Crear un cupón de 10% de descuento:
```
Código: DESCUENTO10
Descripción: 10% de descuento en toda la compra
Valor: 10
Tipo: PERCENTAGE
Fecha inicio: 2024-01-01 00:00
Fecha expiración: 2024-12-31 23:59
Máximo usos: 100
Valor mínimo: 50.00
```

### Crear una regla de descuento fijo:
```
Nombre: Descuento Black Friday
Descripción: $20 de descuento en compras mayores a $100
Valor: 20
Tipo: FIXED_AMOUNT
Valor mínimo: 100.00
Fecha inicio: 2024-11-24 00:00
Fecha expiración: 2024-11-30 23:59
```

## Notas Técnicas

- El sistema mantiene compatibilidad con el código existente
- Los descuentos se aplican en el orden: cupón + regla
- El total nunca puede ser negativo (mínimo 0)
- Los cupones se marcan como usados automáticamente al aplicar
- Las reglas se evalúan dinámicamente según el valor del pedido
- El sistema es extensible para futuras funcionalidades







