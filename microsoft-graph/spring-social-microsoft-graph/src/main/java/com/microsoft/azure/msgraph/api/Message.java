/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    private java.util.Calendar lastModifiedDateTime;

    private java.util.Calendar createdDateTime;

    private java.util.Calendar receivedDateTime;

    private java.util.Calendar sentDateTime;

    private Boolean hasAttachments;

    private String internetMessageId;

    private String id;

    private String subject;

    private ItemBody body;

    private String bodyPreview;

    private Importance importance;

    private String parentFolderId;

    private Recipient sender;

    private Recipient from;

    private java.util.List<Recipient> toRecipients;

    private java.util.List<Recipient> ccRecipients;

    private java.util.List<String> categories;

    private java.util.List<Recipient> bccRecipients;

    private java.util.List<Recipient> replyTo;

    private String changeKey;

    private String conversationId;

    private Boolean isDeliveryReceiptRequested;

    private Boolean isReadReceiptRequested;

    private Boolean isRead;

    private Boolean isDraft;

    private String webLink;

    private InferenceClassificationType inferenceClassification;

    private ItemBody uniqueBody;

    /**
     * The date and time the message was last changed.
     */
    public java.util.Calendar getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(java.util.Calendar lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    /**
     * The date and time the message was created.
     */
    public java.util.Calendar getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(java.util.Calendar createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * The Received Date Time.
     */
    public java.util.Calendar getReceivedDateTime() {
        return receivedDateTime;
    }

    public void setReceivedDateTime(java.util.Calendar receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }

    /**
     * The Sent Date Time.
     */
    public java.util.Calendar getSentDateTime() {
        return sentDateTime;
    }

    public void setSentDateTime(java.util.Calendar sentDateTime) {
        this.sentDateTime = sentDateTime;
    }

    /**
     * The Has Attachments.
     */
    public Boolean getHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(Boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    /**
     * The Internet Message Id.
     */
    public String getInternetMessageId() {
        return internetMessageId;
    }

    public void setInternetMessageId(String internetMessageId) {
        this.internetMessageId = internetMessageId;
    }

    /**
     * Unique identifier for the message.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The Subject.
     */
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * The Body.
     */
    public ItemBody getBody() {
        return body;
    }

    public void setBody(ItemBody body) {
        this.body = body;
    }

    /**
     * The Body Preview.
     */
    public String getBodyPreview() {
        return bodyPreview;
    }

    public void setBodyPreview(String bodyPreview) {
        this.bodyPreview = bodyPreview;
    }

    /**
     * The Importance.
     */
    public Importance getImportance() {
        return importance;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }

    /**
     * The Parent Folder Id.
     */
    public String getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(String parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    /**
     * The Sender.
     */
    public Recipient getSender() {
        return sender;
    }

    public void setSender(Recipient sender) {
        this.sender = sender;
    }

    /**
     * The From.
     */
    public Recipient getFrom() {
        return from;
    }

    public void setFrom(Recipient from) {
        this.from = from;
    }

    /**
     * The To Recipients.
     */
    public java.util.List<Recipient> getToRecipients() {
        return toRecipients;
    }

    public void setToRecipients(java.util.List<Recipient> toRecipients) {
        this.toRecipients = toRecipients;
    }

    /**
     * The Cc Recipients.
     */
    public java.util.List<Recipient> getCcRecipients() {
        return ccRecipients;
    }

    public void setCcRecipients(java.util.List<Recipient> ccRecipients) {
        this.ccRecipients = ccRecipients;
    }

    /**
     * The categories associated with the message.
     */
    public java.util.List<String> getCategories() {
        return categories;
    }

    public void setCategories(java.util.List<String> categories) {
        this.categories = categories;
    }

    /**
     * The Bcc Recipients.
     */
    public java.util.List<Recipient> getBccRecipients() {
        return bccRecipients;
    }

    public void setBccRecipients(java.util.List<Recipient> bccRecipients) {
        this.bccRecipients = bccRecipients;
    }

    /**
     * The Reply To.
     */
    public java.util.List<Recipient> getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(java.util.List<Recipient> replyTo) {
        this.replyTo = replyTo;
    }

    /**
     * The version of the message.
     */
    public String getChangeKey() {
        return changeKey;
    }

    public void setChangeKey(String changeKey) {
        this.changeKey = changeKey;
    }

    /**
     * The Conversation Id.
     */
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * The Is Delivery Receipt Requested.
     */
    public Boolean getIsDeliveryReceiptRequested() {
        return isDeliveryReceiptRequested;
    }

    public void setIsDeliveryReceiptRequested(Boolean deliveryReceiptRequested) {
        isDeliveryReceiptRequested = deliveryReceiptRequested;
    }

    /**
     * The Is Read Receipt Requested.
     */
    public Boolean getIsReadReceiptRequested() {
        return isReadReceiptRequested;
    }

    public void setIsReadReceiptRequested(Boolean readReceiptRequested) {
        isReadReceiptRequested = readReceiptRequested;
    }

    /**
     * The Is Read.
     */
    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }

    /**
     * The Is Draft.
     */
    public Boolean getIsDraft() {
        return isDraft;
    }

    public void setIsDraft(Boolean draft) {
        isDraft = draft;
    }

    /**
     * The Web Link.
     */
    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    /**
     * The Inference Classification.
     */
    public InferenceClassificationType getInferenceClassification() {
        return inferenceClassification;
    }

    public void setInferenceClassification(InferenceClassificationType inferenceClassification) {
        this.inferenceClassification = inferenceClassification;
    }

    /**
     * The Unique Body.
     */
    public ItemBody getUniqueBody() {
        return uniqueBody;
    }

    public void setUniqueBody(ItemBody uniqueBody) {
        this.uniqueBody = uniqueBody;
    }
}
