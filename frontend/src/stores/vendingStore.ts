import { defineStore } from 'pinia'
import {
  insertCoin as apiInsertCoin,
  getSession,
  purchase as apiPurchase,
  reset as apiReset
} from '../services/api'

import type {
  Coin,
  PurchaseResponse
} from '../services/api'

function getErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    return error.message
  }

  return 'An unexpected error occurred'
}

export const useVendingStore = defineStore('vending', {
  state: () => ({
    insertedAmount: 0,
    loading: false,
    error: null as string | null,
    lastPurchase: null as PurchaseResponse | null
  }),

  actions: {
    async loadSession(): Promise<void> {
      try {
        this.error = null

        this.insertedAmount = await getSession()
      } catch (error: unknown) {
        this.error = getErrorMessage(error)
      }
    },

    async insertCoin(coin: Coin): Promise<void> {
      try {
        this.loading = true
        this.error = null

        this.insertedAmount = await apiInsertCoin(coin)
      } catch (error: unknown) {
        this.error = getErrorMessage(error)
      } finally {
        this.loading = false
      }
    },
    async purchase(productId: string): Promise<PurchaseResponse> {
      try {
        this.loading = true
        this.error = null

        const result = await apiPurchase(productId)

        this.insertedAmount = 0
        this.lastPurchase = result

        return result
      } catch (error: unknown) {
        this.error = getErrorMessage(error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async reset(): Promise<Coin[]> {
      try {
        this.loading = true
        this.error = null

        const returnedCoins = await apiReset()

        this.insertedAmount = 0

        return returnedCoins
      } catch (error: unknown) {
        this.error = getErrorMessage(error)
        throw error
      } finally {
        this.loading = false
      }
    },

    clearMessage(): void  {
      this.lastPurchase = null
      this.error = null
    }
  }
})