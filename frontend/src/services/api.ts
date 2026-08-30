const API_BASE_URL = 'http://localhost:8080/api'

export interface Product {
  id: string
  name: string
  price: number
  quantity: number
}

export interface PurchaseResponse {
  product: Product
  paidAmount: number
  changeAmount: number
  changeCoins: string[]
}

export type Coin =
  | 'ONE_CENT'
  | 'TWO_CENTS'
  | 'FIVE_CENTS'
  | 'TEN_CENTS'
  | 'TWENTY_CENTS'
  | 'FIFTY_CENTS'
  | 'ONE_EURO'
  | 'TWO_EURO'

interface ProblemDetail {
  type?: string
  title?: string
  status?: number
  detail?: string
  instance?: string
}

async function request<T>(
  url: string,
  options: RequestInit = {}
): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${url}`, {
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    ...options
  })

  if (!response.ok) {
    let message = `Request failed with status ${response.status}`

    try {
      const problem = (await response.json()) as ProblemDetail

      if (problem.detail) {
        message = problem.detail
      } else if (problem.title) {
        message = problem.title
      }
    } catch {
      // Response body was not valid JSON
    }

    throw new Error(message)
  }

  if (response.status === 204) {
    return null as T
  }

  return response.json() as Promise<T>
}

export function getProducts(): Promise<Product[]> {
  return request<Product[]>('/products')
}

export function getProduct(id: string): Promise<Product> {
  return request<Product>(`/products/${id}`)
}

export function insertCoin(coin: Coin): Promise<number> {
  return request<number>('/vending/coins', {
    method: 'POST',
    body: JSON.stringify(coin)
  })
}

export function getSession(): Promise<number> {
  return request<number>('/vending/session')
}

export function purchase(
  productId: string
): Promise<PurchaseResponse> {
  return request<PurchaseResponse>(
    `/vending/purchase/${productId}`,
    {
      method: 'POST'
    }
  )
}

export function reset(): Promise<Coin[]> {
  return request<Coin[]>('/vending/reset', {
    method: 'POST'
  })
}

export interface ProductRequest {
  name: string
  price: number
  quantity: number
}

export function createProduct(
  product: ProductRequest
): Promise<Product> {
  return request<Product>('/products', {
    method: 'POST',
    body: JSON.stringify(product)
  })
}

export function updateProduct(
  id: string,
  product: ProductRequest
): Promise<Product> {
  return request<Product>(`/products/${id}`, {
    method: 'PUT',
    body: JSON.stringify(product)
  })
}

export function deleteProduct(id: string): Promise<void> {
  return request<void>(`/products/${id}`, {
    method: 'DELETE'
  })
}