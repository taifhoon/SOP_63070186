package com.example.sop_63070186.controller;

import com.example.sop_63070186.pojo.Product;
import com.example.sop_63070186.repository.ProductService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @RabbitListener(queues = "AddProductQueue")
    public boolean serviceAddProduct(Product p){
        return productService.addProduct(p);
    }
    @RabbitListener(queues = "UpdateProductQueue")
    public boolean serviceUpdateProduct(Product product){
        return productService.updateProduct(product);
    }
    @RabbitListener(queues = "DeleteProductQueue")
    public boolean serviceDeleteProduct(Product product){
        return productService.deleteProduct(product);
    }
    @RabbitListener(queues = "GetNameProductQueue")
    public Product serviceGetProductName(String productName){
        return productService.getProductByName(productName);
    }
    @RabbitListener(queues = "GetAllProductQueue")
    public List<Product> serviceGetAllProduct(){
        return productService.getAllProduct();
    }
}
