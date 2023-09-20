package net.etfbl.model;

import java.util.ArrayList;
import java.util.Objects;

import net.etfbl.api.ProductsAPIService;

public class Product {
	public static ArrayList<Product> predefinedProducts;
	static {
		predefinedProducts = new ArrayList<Product>();
		predefinedProducts.add(new Product(1, "Krem banana"));
		predefinedProducts.add(new Product(2, "Twix"));
		predefinedProducts.add(new Product(3, "Snickers"));
		predefinedProducts.add(new Product(4, "Kiki bombone"));
		predefinedProducts.add(new Product(5, "Haribo bombone"));
		predefinedProducts.add(new Product(6, "Kinder jaje"));
		
		ProductsAPIService productService = new ProductsAPIService();
		
		for(Product p : predefinedProducts) {
			productService.addProduct(p);
		}
	}
	private int productId;
	private String productName;
	
	public Product() {
	}
	
	public Product(int productId, String productName) {
		this.productId = productId;
		this.productName = productName;
	}
	
	public int getProductId() {
		return productId;
	}
	
	public void setProductId(int productId) {
		this.productId = productId;
	}
	
	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Override
	public String toString() {
		return "Product [productId=" + productId + ", productName=" + productName + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(productId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return productId == other.productId;
	}
}
