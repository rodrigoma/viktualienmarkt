package com.moip.hackday.jbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.ramswaroop.jbot.core.slack.models.Attachment;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ButtonAttachment extends Attachment {

    @JsonProperty("callback_id")
    private String callbackId;
    private Action[] actions;
    @JsonProperty("attachment_type")
    private String attachmentType;

    public Action[] getActions() {
        return actions;
    }

    public void setActions(Action[] actions) {
        this.actions = actions;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }
}