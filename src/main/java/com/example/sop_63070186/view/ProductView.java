package com.example.sop_63070186.view;

import com.example.sop_63070186.pojo.Product;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Route(value = "/index")
public class ProductView extends VerticalLayout {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    ProductView(){
        ComboBox<String> productList = new ComboBox<>("Product List");
        productList.setWidth("600px");

        TextField tname = new TextField("Product Name");
        tname.setValue("");
        tname.setWidth("600px");

        NumberField numcost = new NumberField("Product Cost:");
        NumberField numprofit = new NumberField("Product Profit:");
        NumberField numprice = new NumberField("Product Price   :");
        numcost.setWidth("600px");
        numcost.setValue(0.0);
        numprofit.setWidth("600px");
        numprofit.setValue(0.0);
        numprice.setWidth("600px");
        numprice.setValue(0.0);
        numprice.setEnabled(false);

        Button butAdd = new Button("Add Product");
        Button butUpdate = new Button("Update Product");
        Button butDelete = new Button("Delete Product");
        Button butClear = new Button("Clear Product");
        HorizontalLayout h1 = new HorizontalLayout();
        h1.add(butAdd, butUpdate, butDelete, butClear);
        add(productList, tname, numcost, numprofit, numprice, h1);

        productList.addFocusListener(e -> {
            List<Product> allProduct = (List<Product>) rabbitTemplate.convertSendAndReceive("ProductExchange", "getall", "");
            List<String> nameProduct = new ArrayList<>();
            for (Product p:allProduct) {
                nameProduct.add(p.getProductName());
            };
            productList.setItems(nameProduct);
        });
        productList.addValueChangeListener(e -> {
            String nameProduct = productList.getValue();
            if (nameProduct != null) {
                Product p = (Product) rabbitTemplate.convertSendAndReceive("ProductExchange", "getname", nameProduct);
                tname.setValue(p.getProductName());
                numcost.setValue(p.getProductCost());
                numprice.setValue(p.getProductPrice());
                numprofit.setValue(p.getProductProfit());
            }
        });

        numprofit.addKeyPressListener(e -> {
            if (e.getKey().toString().equals("Enter")) {
                double price = WebClient.create()
                        .get()
                        .uri("http://localhost:8080/getPrice/" + numcost.getValue() + "/" + numprofit.getValue())
                        .retrieve()
                        .bodyToMono(double.class)
                        .block();
                numprice.setValue(price);
            }
        });
        numcost.addKeyPressListener(e -> {
            if (e.getKey().toString().equals("Enter")) {
                double price = WebClient.create()
                        .get()
                        .uri("http://localhost:8080/getPrice/" + numcost.getValue() + "/" + numprofit.getValue())
                        .retrieve()
                        .bodyToMono(double.class)
                        .block();
                numprice.setValue(price);
            }
        });

        butAdd.addClickListener(e -> {
            double price = WebClient.create()
                    .get()
                    .uri("http://localhost:8080/getPrice/" + numcost.getValue() + "/" + numprofit.getValue())
                    .retrieve()
                    .bodyToMono(double.class)
                    .block();
            numprice.setValue(price);
            Product p = new Product(null, tname.getValue(), numcost.getValue(), numprofit.getValue(), numprice.getValue());
            boolean check = (boolean) rabbitTemplate.convertSendAndReceive("ProductExchange", "add", p);
            if (check){
                Notification.show("Add Product Complete", 500, Notification.Position.BOTTOM_START);
            }
        });

        butUpdate.addClickListener(e -> {
            double price = WebClient.create()
                    .get()
                    .uri("http://localhost:8080/getPrice/" + numcost.getValue() + "/" + numprofit.getValue())
                    .retrieve()
                    .bodyToMono(double.class)
                    .block();
            numprice.setValue(price);
            if (productList.getValue() != null) {
                Product product = (Product) rabbitTemplate.convertSendAndReceive("ProductExchange", "getname", productList.getValue());
                Product upProduct = new Product(product.get_id(), tname.getValue(), numcost.getValue(), numprofit.getValue(), numprice.getValue());
                boolean check = (boolean) rabbitTemplate.convertSendAndReceive("ProductExchange", "update", upProduct);
                if (check) {
                    Notification.show("Update Product Complete", 500, Notification.Position.BOTTOM_START);
                }
            }
            List<Product> allProduct = (List<Product>) rabbitTemplate.convertSendAndReceive("ProductExchange", "getall", "");
            List<String> nameProduct = new ArrayList<>();
            for (Product p:allProduct) {
                nameProduct.add(p.getProductName());
            };
            productList.setItems(nameProduct);
        });
        butDelete.addClickListener(e -> {
            String nameProduct = productList.getValue();
            Product selectProduct = (Product) rabbitTemplate.convertSendAndReceive("ProductExchange", "getname", nameProduct);
            boolean check = (boolean) rabbitTemplate.convertSendAndReceive("ProductExchange", "delete", selectProduct);
            if (check){
                Notification.show("Delete Product Complete", 500, Notification.Position.BOTTOM_START);
            }
        });
        butClear.addClickListener(e -> {
            tname.setValue("");
            numcost.setValue(0.0);
            numprice.setValue(0.0);
            numprofit.setValue(0.0);
            Notification.show("Clear Product Complete", 500, Notification.Position.BOTTOM_START);
        });
    }
}
