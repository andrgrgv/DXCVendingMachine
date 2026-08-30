<script setup lang="ts">
import { onMounted, ref } from 'vue'
import ProductCard from './ProductCard.vue'
import { getProducts } from '../services/api'
import type { Product } from '../services/api'

const products = ref<Product[]>([])
const loading = ref<boolean>(false)
const error = ref<string | null>(null)

function getErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    return error.message
  }

  return 'An unexpected error occurred'
}

async function loadProducts(): Promise<void> {
  try {
    loading.value = true
    error.value = null

    products.value = await getProducts()
  } catch (e: unknown) {
    error.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

function updateProduct(updatedProduct: Product): void {
  const index = products.value.findIndex(
    product => product.id === updatedProduct.id
  )

  if (index !== -1) {
    products.value[index] = updatedProduct
  }
}

onMounted(loadProducts)
</script>

<template>
  <section class="products-section">
    <h2>Products</h2>

    <p v-if="loading">
      Loading products...
    </p>

    <p
      v-else-if="error"
      class="error-box"
    >
      {{ error }}
    </p>

    <div
      v-else
      class="grid grid-2"
    >
      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
        @purchased="updateProduct"
      />
    </div>
  </section>
</template>

<style scoped>
.products-section {
  width: 100%;
}

.products-section h2 {
  margin-top: 0;
}
</style>
