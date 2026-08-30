<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useVendingStore } from '../stores/vendingStore'
import type { Coin } from '../services/api'

const vendingStore = useVendingStore()

const {
  insertedAmount,
  loading,
  error
} = storeToRefs(vendingStore)

const coins: { value: Coin; label: string }[] = [
  { value: 'ONE_CENT', label: '€0.01' },
  { value: 'TWO_CENTS', label: '€0.02' },
  { value: 'FIVE_CENTS', label: '€0.05' },
  { value: 'TEN_CENTS', label: '€0.10' },
  { value: 'TWENTY_CENTS', label: '€0.20' },
  { value: 'FIFTY_CENTS', label: '€0.50' },
  { value: 'ONE_EURO', label: '€1.00' },
  { value: 'TWO_EURO', label: '€2.00' }
]

async function reset(): Promise<void> {
  try {
    await vendingStore.reset()
  } catch {
    // Error is already stored in vendingStore.error
  }
}
</script>

<template>
  <section class="card stack payment-panel">
    <h2>Payment</h2>

    <div class="amount">
      €{{ Number(insertedAmount).toFixed(2) }}
    </div>

    <div class="grid grid-2">
      <button
        v-for="coin in coins"
        :key="coin.value"
        class="btn"
        :disabled="loading"
        @click="vendingStore.insertCoin(coin.value)"
      >
        {{ coin.label }}
      </button>
    </div>

    <button
      class="btn btn-full"
      :disabled="loading || insertedAmount <= 0"
      @click="reset"
    >
      Return Coins
    </button>

    <p
      v-if="error"
      class="error-box"
    >
      {{ error }}
    </p>
  </section>
</template>

<style scoped>
.payment-panel h2 {
  margin: 0;
}

.amount {
  font-size: 2rem;
  font-weight: 700;
}

</style>
