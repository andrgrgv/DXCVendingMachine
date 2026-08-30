<script setup lang="ts">
import { computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useVendingStore } from '../stores/vendingStore'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['purchased'])

const vendingStore = useVendingStore()

const { insertedAmount, loading } = storeToRefs(vendingStore)

const canPurchase = computed(() => {
  return (
    props.product.quantity > 0 &&
    Number(insertedAmount.value) >= Number(props.product.price)
  )
})

async function buy() {
  try {
    const result = await vendingStore.purchase(props.product.id)

    emit('purchased', result.product)
  } catch (error) {
    // Error is stored in vendingStore.error
  }
}
</script>

<template>
  <article class="card product-card">

    <div class="stack">
      <h3 class="product-name">
        {{ product.name }}
      </h3>

      <p class="product-price">
        €{{ Number(product.price).toFixed(2) }}
      </p>

      <p
        class="product-stock text-muted"
        :class="{ 'text-danger': product.quantity === 0 }"
      >
        <template v-if="product.quantity > 0">
          {{ product.quantity }} available
        </template>

        <template v-else>
          Out of stock
        </template>
      </p>
    </div>

    <button
      class="btn btn-full buy-button"
      :disabled="!canPurchase || loading"
      @click="buy"
    >
      {{ loading ? 'Processing...' : 'Buy' }}
    </button>

  </article>
</template>

<style scoped>
.product-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 180px;
}

.product-name {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
}

.product-price {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
}

.product-stock {
  margin: 0;
  font-size: 0.9rem;
}

.buy-button {
  margin-top: 1.25rem;
}
</style>
