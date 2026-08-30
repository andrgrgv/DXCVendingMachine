<script setup lang="ts">
import { onMounted } from 'vue'
import ProductGrid from '../components/ProductGrid.vue'
import PaymentPanel from '../components/PaymentPanel.vue'
import MessageBox from '../components/MessageBox.vue'
import { useVendingStore } from '../stores/vendingStore'

const vendingStore = useVendingStore()

onMounted(() => {
  vendingStore.loadSession()
})
</script>

<template>
  <main class="machine">

    <header class="machine-header">
      <h1>Vending Machine</h1>
      <p>Select a product and insert coins</p>
    </header>

    <div class="machine-content">
      <ProductGrid />
      <PaymentPanel />
    </div>

    <MessageBox />

  </main>
</template>