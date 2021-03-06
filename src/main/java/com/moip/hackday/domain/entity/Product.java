package com.moip.hackday.domain.entity;

import me.ramswaroop.jbot.core.slack.models.Action;
import me.ramswaroop.jbot.core.slack.models.Attachment;
import me.ramswaroop.jbot.core.slack.models.Field;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Product {

    @Id
    private String id = ObjectId.get().toString();

    private String name;

    private String price;

    private String url = "http://i.imgur.com/ZR1TyFJ.jpg";

    private String sellerName;

    public Product setId(String id) {
        this.id = id;
        return this;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public Product setPrice(String price) {
        this.price = price;
        return this;
    }

    public Product setUrl(String url) {
        this.url = url;
        return this;
    }

    public Product setSellerName(String sellerName) {
        this.sellerName = sellerName;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }

    public String getSellerName() {
        return sellerName;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", url='" + url + '\'' +
                ", sellerName='" + sellerName + '\'' +
                '}';
    }

    public List<Attachment> toAttachments(String userId, String userName) {
        List<Attachment> attachmentList = new ArrayList();

        Attachment productAtt = new Attachment();
        productAtt.setFallback("");
        productAtt.setText("");
        productAtt.setAuthorName("@".concat(this.sellerName));
        productAtt.setColor("#cae028");

        Field product = new Field();
        product.setTitle("PRODUCT");
        product.setValue(this.name);
        product.setShortEnough(true);

        Field price = new Field();
        price.setTitle("PRICE");
        price.setValue(this.price);
        price.setShortEnough(true);

        productAtt.setFields(new Field[]{product, price});

        attachmentList.add(productAtt);

        Attachment imageAtt = new Attachment();
        imageAtt.setFallback("");
        imageAtt.setText("");
        imageAtt.setTitle("Merely illustrative image");
        imageAtt.setImageUrl(this.url);
        imageAtt.setColor("#439FE0");

        attachmentList.add(imageAtt);

        if (this.sellerName.equals(userId) || this.sellerName.equals(userName)) {
            Attachment buttonAtt = new Attachment();
            buttonAtt.setFallback("");
            buttonAtt.setTitle("");
            buttonAtt.setCallbackId(this.id);
            buttonAtt.setColor("#e0283a");
            buttonAtt.setAttachmentType("default");

            Action action = new Action();
            action.setName("solded");
            action.setText("Mark as solded");
            action.setType("button");
            action.setValue("solded");

            buttonAtt.setActions(new Action[]{action});

            attachmentList.add(buttonAtt);
        }

        return attachmentList;
    }
}