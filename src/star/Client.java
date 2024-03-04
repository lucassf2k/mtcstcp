package star;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import shared.Message;

public class Client implements Runnable {
  public static Socket client;
  private final String processName;
  public static PrintStream printer;

  public Client(final Socket client, final String name) {
    Client.client = client;
    this.processName = name;
  }

  @Override
  public void run() {
    try {
      final var scan = new Scanner(System.in);
      System.out.println("o cliente " + Process.PORT + " conectou ao servidor");
      printer = new PrintStream(client.getOutputStream());
      boolean hasConnection = Boolean.TRUE;
      while (hasConnection) {
        System.out.println("exemplos:\nOlá -u=p3 | olá a todos -b\nDigite sua mensagem: ");
        final var rawMessage = scan.nextLine();
        final var formattedMessage = this.formatMessage(rawMessage);
        final var messageContent = formattedMessage[0];
        int recipientPort = 0;
        String recipentName = "";
        String messageSendingType = "";
        if (formattedMessage.length == 2) {
          messageSendingType = formattedMessage[1];
        }
        if (formattedMessage.length == 3) {
          messageSendingType = formattedMessage[1];
          recipentName = formattedMessage[2];
        }
        switch (recipentName.toLowerCase()) {
          case "p1":
            recipientPort = 56001;
            break;
          case "p2":
            recipientPort = 56002;
            break;
          case "p3":
            recipientPort = 56003;
            break;
          case "p4":
            recipientPort = 56004;
            break;
        }
        final var message = new Message(messageContent, Process.PORT, recipientPort, messageSendingType);
        System.out.println(message.toString());
        if (messageContent.equalsIgnoreCase("fim")) {
          System.out.println("encerrando cliente de " + Process.PORT);
          this.sendMessage(message);
          hasConnection = Boolean.FALSE;
        } else {
          final var recipient = message.getRecipient() == 0 ? "all" : message.getRecipient();
          System.out.println("enviando ao servidor de " + recipient);
          this.sendMessage(message);
        }
      }
      scan.close();
      printer.close();
      client.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void sendMessage(final Message message) {
    Client.printer.println(
        message.getContent() + "-" + message.getRecipient() + "-" + message.getSender() + "-" + message.getType());
  }

  private String[] formatMessage(final String rawMessage) {
    final var splitMessage = rawMessage.split("-");
    if (splitMessage.length != 2) {
      final var output = new String[1];
      output[0] = splitMessage[0];
      return output;
    }
    final var infos = splitMessage[1].split("=");
    if (infos.length != 2) {
      final var output = new String[2];
      output[0] = splitMessage[0];
      output[1] = splitMessage[1];
      return output;
    }
    final var messageContent = splitMessage[0];
    final var output = new String[3];
    output[0] = messageContent;
    output[1] = infos[0];
    output[2] = infos[1];
    return output;
  }

  public static Socket getClient() {
    return client;
  }

  public static void setClient(Socket client) {
    Client.client = client;
  }

  public String getProcessName() {
    return processName;
  }
}
