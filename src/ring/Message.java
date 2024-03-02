package ring;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  private String content;
  private int sender;
  private int recipientPort;
  private String type;

  public Message(
      final String content,
      final int sender,
      final int recipientPort,
      final String type) {
    this.content = content;
    this.sender = sender;
    this.recipientPort = recipientPort;
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(final String content) {
    this.content = content;
  }

  public int getSender() {
    return sender;
  }

  public void setSender(final int sender) {
    this.sender = sender;
  }

  public int getRecipient() {
    return recipientPort;
  }

  public void setRecipient(final int recipientPort) {
    this.recipientPort = recipientPort;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "Message [content=" + content + ", sender=" + sender + ", recipientPort=" + recipientPort + ", type=" + type
        + "]";
  }
}
