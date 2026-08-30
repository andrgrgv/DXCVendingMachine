<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  getProducts,
  createProduct,
  updateProduct,
  deleteProduct
} from '../services/api'

import type { Product } from '../services/api'

const products = ref<Product[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const editingId = ref<string | null>(null)

const form = ref({
  name: '',
  price: 0,
  quantity: 0
})

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

function startEdit(product: Product): void {
  editingId.value = product.id

  form.value = {
    name: product.name,
    price: Number(product.price),
    quantity: product.quantity
  }
}

function cancelEdit(): void {
  editingId.value = null

  form.value = {
    name: '',
    price: 0,
    quantity: 0
  }
}

async function saveProduct(): Promise<void> {
  try {
    loading.value = true
    error.value = null

    if (editingId.value) {
      const updated = await updateProduct(editingId.value, {
        name: form.value.name,
        price: form.value.price,
        quantity: form.value.quantity
      })

      const index = products.value.findIndex(
        product => product.id === updated.id
      )

      if (index !== -1) {
        products.value[index] = updated
      }
    } else {
      const created = await createProduct({
        name: form.value.name,
        price: form.value.price,
        quantity: form.value.quantity
      })

      products.value.push(created)
    }

    cancelEdit()
  } catch (e: unknown) {
    error.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

async function removeProduct(id: string): Promise<void> {
  try {
    loading.value = true
    error.value = null

    await deleteProduct(id)

    products.value = products.value.filter(
      product => product.id !== id
    )

    if (editingId.value === id) {
      cancelEdit()
    }
  } catch (e: unknown) {
    error.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

onMounted(loadProducts)
</script>

<template>
  <main class="admin">
    <header class="admin-header">
      <div>
        <h1>Product Administration</h1>
        <p class="text-muted">Manage vending machine products and stock.</p>
      </div>

      <RouterLink class="back-link" to="/">
        Back to vending machine
      </RouterLink>
    </header>

    <section class="card admin-card">
      <h2>
        {{ editingId ? 'Edit Product' : 'Add Product' }}
      </h2>

      <form class="product-form" @submit.prevent="saveProduct">
        <label>
          Name
          <input
            v-model.trim="form.name"
            type="text"
            required
          />
        </label>

        <label>
          Price
          <input
            v-model.number="form.price"
            type="number"
            min="0.01"
            step="0.01"
            required
          />
        </label>

        <label>
          Quantity
          <input
            v-model.number="form.quantity"
            type="number"
            min="0"
            max="15"
            step="1"
            required
          />
        </label>

        <div class="form-actions">
          <button
            class="btn primary-button"
            type="submit"
            :disabled="loading"
          >
            {{ editingId ? 'Save Changes' : 'Add Product' }}
          </button>

          <button
            v-if="editingId"
            class="btn secondary-button"
            type="button"
            :disabled="loading"
            @click="cancelEdit"
          >
            Cancel
          </button>
        </div>
      </form>
    </section>

    <p v-if="error" class="error-box admin-error">
      {{ error }}
    </p>

    <section class="card admin-card">
      <div class="section-header">
        <h2>Products</h2>

        <span class="text-muted">{{ products.length }} products</span>
      </div>

      <p v-if="loading && products.length === 0">
        Loading products...
      </p>

      <div v-else class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Price</th>
              <th>Quantity</th>
              <th class="actions-column">Actions</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="product in products"
              :key="product.id"
            >
              <td>{{ product.name }}</td>

              <td>
                &euro;{{ Number(product.price).toFixed(2) }}
              </td>

              <td>
                {{ product.quantity }}
              </td>

              <td class="actions">
                <button
                  class="btn edit-button"
                  :disabled="loading"
                  @click="startEdit(product)"
                >
                  Edit
                </button>

                <button
                  class="btn delete-button"
                  :disabled="loading"
                  @click="removeProduct(product.id)"
                >
                  Delete
                </button>
              </td>
            </tr>

            <tr v-if="products.length === 0">
              <td colspan="4" class="empty text-muted">
                No products available.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>

<style scoped>
.admin {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: 2rem;
}

.admin-header,
.section-header,
.actions,
.form-actions {
  display: flex;
  gap: 1rem;
}

.admin-header,
.section-header {
  align-items: center;
  justify-content: space-between;
}

.admin-header {
  margin-bottom: 2rem;
}

.admin-header h1,
.admin-header p,
.admin-card h2 {
  margin: 0;
}

.admin-card {
  margin-bottom: 1.5rem;
}

.back-link {
  font-weight: 600;
  text-decoration: none;
}

.product-form {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
  gap: 1rem;
  align-items: end;
  margin-top: 1rem;
}

.product-form label {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  font-weight: 600;
}

.product-form input {
  padding: 0.7rem 0.8rem;
  border: 1px solid #d1d5db;
  border-radius: var(--radius-md);
  font: inherit;
}

.product-form input:focus {
  outline: 2px solid #93c5fd;
  border-color: #60a5fa;
}

.primary-button {
  background: #2563eb;
  color: white;
}

.secondary-button {
  background: #e5e7eb;
  color: #111827;
}

.edit-button {
  background: #e0f2fe;
  color: #075985;
}

.delete-button {
  background: #fee2e2;
  color: #991b1b;
}

.admin-error {
  margin-bottom: 1.5rem;
}

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 0.9rem 0.75rem;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

th {
  font-size: 0.9rem;
  color: var(--color-muted);
}

.actions-column {
  width: 180px;
}

.actions {
  gap: 0.5rem;
}

.empty {
  padding: 2rem;
  text-align: center;
}

@media (max-width: 800px) {
  .admin {
    padding: 1rem;
  }

  .admin-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .product-form {
    grid-template-columns: 1fr;
  }

  .form-actions {
    width: 100%;
  }

  .form-actions button {
    flex: 1;
  }
}
</style>
