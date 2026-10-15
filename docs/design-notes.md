# Domain design proposal

Write your first proposal here before implementing the endpoints. See the current exercise in [the learning guide](../LEARNING_GUIDE.md).

## Assumptions and use cases

TODO: What can a customer do in the first version? 

## Models and relationships

TODO: Propose Product, Order, and OrderItem fields, types, identifiers, and relationships.
Product: 
- id : String
- name: String
- description : String
- unit_price: BigDecimal
- image: String
// meta data for entity
- createdAt
- updatedAt

Order:
- id: String
- userId: String
- List<OrderItem> orderLineItems
- status : enum (COMPLETED, FAILED, ACCEPTED, CREATED)
- total_amount: BigDecimal
// meta data for entity
- createdAt
- updatedAt
- version


OrderItem
- id
- orderId
- product_id: String
- product_name_snapshot: String
- unit_price: BigDecimal
- quantity : Integer
- total_amount: BigDecimal
- pricingSnapshot: PricingSnapshot
// meta data for entity
- createdAt
- updatedAt

RelationShips: 
Order - orderLineItem 1- many
OrderLineItem - product many - 1

## Business rules

TODO: Define valid quantities, money/currency rules, price-change behavior, and order totals.
An order must contain at least one item.
Quantity must be present and greater than zero.
If any requested product doesn’t exist, reject the whole order.
The order request supplies product IDs and quantities; the server calculates prices and totals.

## Service boundaries and API contracts

TODO: Describe ownership, request/response examples, and failure behavior.

## Relational schema sketch

TODO: Propose tables, keys, and constraints. This is a design exercise; persistence is not required yet.

## Decisions and tradeoffs

TODO: Record choices after discussion and review.
