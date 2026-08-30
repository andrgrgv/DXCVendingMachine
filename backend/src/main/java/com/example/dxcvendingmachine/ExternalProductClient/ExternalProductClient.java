package com.example.dxcvendingmachine.ExternalProductClient;

import com.example.dxcvendingmachine.Dto.ProductDto;

import java.util.List;

public interface ExternalProductClient {

    List<ProductDto> getProducts();
}