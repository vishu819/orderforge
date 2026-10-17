# Error handling contract

Order Service exposes `code`, `message`, `detailedMessage`, optional `downstreamCode`, and `traceId` (an error correlation identifier, using the MDC traceId if available, otherwise generated). No distributed tracing or request-wide tracing filter is configured yet.

| Code | Meaning | HTTP status |
| --- | --- | --- |
| ORDR-001 | Invalid order request | 400 |
| ORDR-002 | Referenced product not found | 422 |
| ORDR-003 | Product Service unreachable, timed out, throttled, or temporarily unavailable | 503 |
| ORDR-004 | Unexpected or invalid Product Service response | 502 |
| ORDR-005 | Unexpected application failure | 500 |
| ORDR-006 | Order not found | 404 |

Spring MVC failures retain their framework HTTP status and headers (such as 405 and Allow), using ORDR-001 for framework client errors. They do not all become 400.

## Responsibilities

OrderService throws application exceptions for invalid business input. ProductClient translates remote exceptions and validates returned product data. GlobalExceptionHandler maps them to the public Order contract. Controllers need not catch these exceptions.

Downstream mappings: 404 maps to ORDR-002; 429/503/504 and connection/read failures map to ORDR-003; other downstream statuses and malformed successful responses map to ORDR-004. In particular a downstream 401 is not forwarded as a customer authentication failure.

For HTTP 404 plus the approved public code PROD-001, ProductClient copies a bounded, nonblank downstream message with no control characters into detailedMessage and retains downstreamCode. Unknown codes and malformed bodies use a fallback. Raw bodies, exception messages, and infrastructure errors are not forwarded. Add approved codes deliberately as Product's contract grows; never match on message text.

Product Service now returns HTTP 404 with this public contract:

```json
{"code":"PROD-001","message":"The requested product does not exist."}
```

Order's corresponding response:

```json
{
  "code": "ORDR-002",
  "message": "A requested product was not found.",
  "detailedMessage": "The requested product does not exist.",
  "downstreamCode": "PROD-001",
  "traceId": "generated-or-existing-trace-id"
}
```

Unexpected failures return a generic detail. Server failures are logged with their cause and correlation identifier in the handler. There are no automatic retries. OrderController supports POST /orders and GET /orders/{orderId}. End-to-end HTTP verification remains pending.

## Manual checks after creating OrderController

- Valid order: returned total matches captured prices and quantities.
- Invalid quantity/empty order: ORDR-001, HTTP 400.
- Unknown product: ORDR-002, HTTP 422, downstreamCode PROD-001 and public downstream message.
- Product Service stopped: ORDR-003, HTTP 503, fallback detail with no raw connection error.
- Malformed JSON: ORDR-001, HTTP 400.

Compilation is verified; these runtime scenarios have not yet been verified.
