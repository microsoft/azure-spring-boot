/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api;

public class Message {
    /**
     * The date and time the message was last changed.
     */
    public java.util.Calendar lastModifiedDateTime;

    /**
     * The date and time the message was created.
     */
    public java.util.Calendar createdDateTime;

    /**
     * The Received Date Time.
     */
    public java.util.Calendar receivedDateTime;

    /**
     * The Sent Date Time.
     */
    public java.util.Calendar sentDateTime;

    /**
     * The Has Attachments.
     */
    public Boolean hasAttachments;

    /**
     * The Internet Message Id.
     */
    public String internetMessageId;

    /**
     * Unique identifier for the message.
     */
    public String id;

    /**
     * The Subject.
     */
    public String subject;

    /**
     * The Body.
     */
    public ItemBody body;

    /**
     * The Body Preview.
     */
    public String bodyPreview;

    /**
     * The Importance.
     */
    public Importance importance;

    /**
     * The Parent Folder Id.
     */
    public String parentFolderId;

    /**
     * The Sender.
     */
    public Recipient sender;

    /**
     * The From.
     */
    public Recipient from;

    /**
     * The To Recipients.
     */
    public java.util.List<Recipient> toRecipients;

    /**
     * The Cc Recipients.
     */
    public java.util.List<Recipient> ccRecipients;

    /**
     * The categories associated with the message.
     */
    public java.util.List<String> categories;

    /**
     * The Bcc Recipients.
     */
    public java.util.List<Recipient> bccRecipients;

    /**
     * The Reply To.
     */
    public java.util.List<Recipient> replyTo;

    /**
     * The version of the message.
     */
    public String changeKey;

    /**
     * The Conversation Id.
     */
    public String conversationId;

    /**
     * The Is Delivery Receipt Requested.
     */
    public Boolean isDeliveryReceiptRequested;

    /**
     * The Is Read Receipt Requested.
     */
    public Boolean isReadReceiptRequested;

    /**
     * The Is Read.
     */
    public Boolean isRead;

    /**
     * The Is Draft.
     */
    public Boolean isDraft;

    /**
     * The Web Link.
     */
    public String webLink;

    /**
     * The Inference Classification.
     */
    public InferenceClassificationType inferenceClassification;

    /**
     * The Unique Body.
     */
    public ItemBody uniqueBody;
}
