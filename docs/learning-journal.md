# OrderForge learning journal

Use this file for revision: questions from implementation, Vishal's reasoning, reviewed explanations, and decisions. Unanswered questions stay pending; assistant explanations do not imply that Vishal has demonstrated understanding.

## How we use this journal

- Ask one or two relevant questions at the current milestone, rather than unrelated interview questions.
- Give Vishal room to answer before recording a reviewed explanation for a new question.
- Record his answer faithfully, label paraphrases, and distinguish it from assistant guidance.
- Return to earlier questions as the system evolves. Update decisions when requirements change.
- Continue with manual verification during the basic build as requested; defer automated test work until requested or agreed for the later production phase.

## 2026-10-16 — Historical price snapshots

**Question:** What should an existing order show after a product's price changes?

**Vishal's answer (paraphrased from the design discussion):** Existing orders should remain unchanged because their prices are saved as snapshots.

**Reviewed explanation:** Each order line captures the product name and unit price at purchase time. Calculate the line total from that captured unit price and quantity. Later reads of an order should not recalculate it from the current catalog.

**Version-1 decision:** Store captured name, unit price, quantity, and total on each order item. Use INR throughout this version.

**Revision prompt:** Why is retaining only `productId` and `quantity` insufficient for order history?

## 2026-10-16 — Snapshot storage

**Vishal's question:** If a price snapshot has more details, can it still be stored in the order-items row?

**Assistant explanation discussed:** A nested Java value object can map to multiple columns in its owner's table. Fixed pricing fields can use columns; a variable list of tax or discount components may use child rows. JSON is another option with query and constraint tradeoffs. A Java object does not automatically require a separate table.

**Version-1 decision:** Use direct price fields without tax or discount components. Database implementation is deferred.

**Revision prompt:** Which requirement would make you introduce a child table for pricing components?

## 2026-10-16 — UI prices and authoritative checkout

**Vishal's question:** Should the price snapshot come from the cart or UI where the order is placed?

**Assistant explanation discussed:** The UI communicates purchase intent and may report an expected price, but the backend validates the price it accepts. Trusting an arbitrary submitted price allows manipulation. Recalculating without confirmation can also surprise a customer if the displayed price has changed.

**Version-1 decision:** Order Service retrieves prices from Product Service during creation and stores snapshots. The request supplies product IDs and quantities, not authoritative prices. There is no quote or customer price-confirmation mechanism yet.

**Future options:** Validate an expected total and require confirmation on mismatch, or create a backend quote with an identifier, expiry, and explicit price-honoring rules. Neither is implemented yet.

**Revision prompt:** How would you distinguish a tampered price from an honest stale price, and what response would the customer see?

## 2026-10-16 — Partial order failure (pending)

**Question for Vishal:** An order requests a keyboard and a mouse. Fetching the keyboard succeeds, but Product Service times out while fetching the mouse. Should we save any order? At what point should we store it, and what should the caller receive?

**Vishal's answer:** Pending.

**Reviewed explanation and decision:** Pending discussion. Existing rule: reject the whole order if any requested product does not exist; distinguish absence from temporary unavailability.

## After the first deployment

The user wants to evolve the deployed basic version toward a production-ready system. This is planned work, not a claim of readiness.

Revisit persistence and migrations; order states and concurrency; checkout quotes and inventory; idempotency and retry behavior; payments and distributed consistency; security; observability; safe delivery; backups and recovery. Define traffic, availability, durability, and recovery requirements before selecting mechanisms.

## New entry template

```text
Date and topic:
Question and concrete scenario:
Vishal's answer:
Reviewed explanation:
Decision and tradeoff:
Implemented, planned, or deferred:
Revision prompt:
```

## 2026-10-17 — Preparing order creation for HTTP

**Implemented:** Validate the whole request before product calls; distinguish invalid orders, missing products, and Product Service failures using application exceptions. Reject empty or inconsistent product responses. Build the order after calculating totals and copy its item list. Configure request builders for Jackson 3 deserialization.

**Verification:** Both services compile on JDK 21. No automated tests run; endpoint behavior and JSON binding have not yet been manually verified. No order storage or OrderController is implemented by this change. HTTP exception mapping is still pending.

**Question for Vishal (pending):** Why should a missing product and a Product Service timeout be represented as different failures? What should a caller do differently for each?

**Vishal's answer:** Pending.

## 2026-10-17 — Layered error handling

**Vishal's requirement:** Order-specific codes and stable messages, with a downstream public explanation in detailedMessage when available.

**Implemented:** OrderError catalog, application exceptions, ProductClient translation, and global HTTP mapping. Product's missing-product response now includes PROD-001. Only recognized public error messages are forwarded; raw diagnostic data stays out of public responses. See [error handling contract](error-handling.md).

**Verification:** JDK 21 compilation; no automated tests. OrderController and manual end-to-end verification are pending.

**Revision question:** Why must a downstream 401 not automatically become a 401 for the customer?

**Vishal's answer:** Pending.

## 2026-10-17 — In-memory order retrieval

**Implemented:** ConcurrentHashMap stores completed orders after all product calls succeed. GET /orders/{orderId} returns the captured order or ORDR-006 with HTTP 404. Retrieval does not call Product Service or recalculate prices.

**Limits:** Data disappears on restart and is not shared across replicas. ConcurrentHashMap makes individual map operations thread-safe; it does not make multi-step workflows transactional.

**Verification:** Compilation checked without automated tests; manual HTTP checks remain pending.
