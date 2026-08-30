<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useVendingStore } from '../stores/vendingStore'

const vendingStore = useVendingStore()

const { lastPurchase, error } = storeToRefs(vendingStore)

const coinLabels: Record<string, string> = {
  ONE_CENT: '€0.01',
  TWO_CENTS: '€0.02',
  FIVE_CENTS: '€0.05',
  TEN_CENTS: '€0.10',
  TWENTY_CENTS: '€0.20',
  FIFTY_CENTS: '€0.50',
  ONE_EURO: '€1.00',
  TWO_EURO: '€2.00'
}
</script>

<template>
  <section
    v-if="lastPurchase || error"
    class="message-box"
  >
    <div
      v-if="lastPurchase"
      class="message success"
    >
      <button class="close-button" @click="vendingStore.clearMessage()">
        ×
      </button>

      <div class="message-icon">
        ✓
      </div>

      <div class="message-content">
        <h3>Purchase successful</h3>

        <p>
          You purchased
          <strong>{{ lastPurchase.product.name }}</strong>.
        </p>

        <div
          v-if="Number(lastPurchase.changeAmount) > 0"
          class="change"
        >
          <span>Change</span>

          <strong>
            €{{ Number(lastPurchase.changeAmount).toFixed(2) }}
          </strong>
        </div>

        <div
          v-if="lastPurchase.changeCoins.length > 0"
          class="returned-coins"
        >
          <span
            v-for="(coin, index) in lastPurchase.changeCoins"
            :key="index"
            class="coin"
          >
            {{ coinLabels[coin] ?? coin }}
          </span>
        </div>

        <p
          v-if="Number(lastPurchase.changeAmount) === 0"
          class="exact"
        >
          Exact amount received.
        </p>
      </div>
    </div>

    <div
      v-if="error"
      class="message error"
    >
      <button class="close-button" @click="vendingStore.clearMessage()">
        ×
      </button>

      <div class="message-icon">
        !
      </div>

      <div class="message-content">
        <h3>Something went wrong</h3>
        <p>{{ error }}</p>
      </div>
    </div>

  </section>
</template>

<style scoped>
.message-box {
  position: fixed;
  top: 1.5rem;
  right: 1.5rem;
  width: min(380px, calc(100vw - 2rem));
  z-index: 1000;
}

.message {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  padding: 1.25rem;
  padding-right: 3rem;
  box-shadow: var(--shadow-floating);
}

.message-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  border: 2px solid currentColor;
  border-radius: 50%;
  font-size: 1.1rem;
  font-weight: 700;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-content h3 {
  margin: 0 0 0.4rem;
  font-size: 1.05rem;
  font-weight: 600;
}

.message-content p {
  margin: 0.25rem 0;
}

.close-button {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  padding: 0;
  border: none;
  border-radius: var(--radius-sm);
  background: transparent;
  color: inherit;
  font-size: 1.4rem;
  line-height: 1;
  cursor: pointer;
  opacity: 0.6;
}

.close-button:hover {
  background: rgba(0, 0, 0, 0.06);
  opacity: 1;
}

.change {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 1rem;
  padding-top: 0.75rem;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.change strong {
  font-size: 1.25rem;
}

.returned-coins {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
  margin-top: 0.75rem;
}

.coin {
  padding: 0.3rem 0.6rem;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid currentColor;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.exact {
  margin-top: 0.75rem !important;
  font-weight: 500;
}

@media (max-width: 600px) {
  .message-box {
    top: 1rem;
    right: 1rem;
    left: 1rem;
    width: auto;
  }

  .message {
    padding: 1rem;
    padding-right: 2.75rem;
  }
}
</style>
