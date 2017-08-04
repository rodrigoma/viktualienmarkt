package com.moip.hackday.domain.entity;

import com.moip.hackday.Action;
import com.moip.hackday.ButtonAttachment;
import me.ramswaroop.jbot.core.slack.models.Attachment;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Product {

    @Id
    private String id = ObjectId.get().toString();

    private String name;

    private String price;

    private String url = "http://www.milleniumfm.com.br/assets/photo-placeholder-ea19bdc940a9ea756fb519b9f3e82a5c.png";

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
        return getName() + " " + getPrice() + " offered by " + getSellerName() + "\n" + getUrl() + "\n \n";
    }

    public ButtonAttachment toAttachment(String userId) {
        ButtonAttachment attachment = new ButtonAttachment();
        attachment.setColor("8B008B");

        attachment.setTitle(name);
        attachment.setText("Quer vender '" + getName() + "' por: " + getPrice());

        attachment.setAuthorName("@" + getSellerName());
        attachment.setAuthorLink("https://moip.slack.com/team/" + getSellerName());

        if (getSellerName().equals(userId)) {
            Action sold = new Action();

            sold.setName("sold");
            sold.setText("Marcar como vendido");
            sold.setType("button");
            sold.setStyle("good");
            sold.setValue("sold");

            attachment.setActions(new Action[1]);
            attachment.getActions()[0] = sold;
        }

        if (!getUrl().isEmpty()) {
            attachment.setThumbUrl(getUrl());
        }

        return attachment;
    }

    public RichMessage toRichMessage() {
        RichMessage richMessage = new RichMessage("Aqui está o produto:");
        Attachment productName = new Attachment();
        Attachment productPrice = new Attachment();
        ButtonAttachment confirmButton = new ButtonAttachment();

        productName.setTitle(getName());
        productName.setImageUrl(getUrl());
        productPrice.setTitle("Preço");
        productPrice.setText(getPrice());

        Action confirm = new Action();
        Action cancel = new Action();

        confirm.setName("confirm");
        confirm.setText("Confirmar");
        confirm.setType("button");
        confirm.setStyle("good");
        confirm.setValue("confirm");

        cancel.setName("cancel");
        cancel.setText("Cancelar");
        cancel.setType("button");
        cancel.setStyle("danger");
        cancel.setValue("cancel");

        Action[] actions = new Action[]{confirm, cancel};
        confirmButton.setActions(actions);
        confirmButton.setFallback("Confirmar");
        confirmButton.setTitle("Confirmar");
        confirmButton.setCallbackId(getId());
        confirmButton.setColor("#3AA3E3");
        confirmButton.setAttachmentType("default");

        Attachment[] attachments = new Attachment[]{productName, productPrice, confirmButton};
        richMessage.setAttachments(attachments);
        return richMessage;
    }
}
