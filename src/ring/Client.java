package ring;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
  private final Socket client;
  private final String processName;
  private static ObjectInputStream input;
  private static ObjectOutputStream output;
  public static int PORT;

  public Client(final Socket client, final String name) {
    this.client = client;
    this.processName = name;
  }

  @Override
  @SuppressWarnings("resource")
  public void run() {
    final var scan = new Scanner(System.in);
    Client.PORT = client.getPort();
    final var port = client.getPort();
    System.out.println("o cliente " + processName + " conectou ao servidor");
    try {
      boolean hasConnection = Boolean.TRUE;
      while (hasConnection) {
        output = new ObjectOutputStream(this.client.getOutputStream());
        input = new ObjectInputStream(this.client.getInputStream());
        System.out.println("Digite sua mensagem: ");
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
            recipientPort = 56004;
            break;
          case "p2":
            recipientPort = 56001;
            break;
          case "p3":
            recipientPort = 56002;
            break;
          case "p4":
            recipientPort = 56003;
        }
        final var message = new Message(messageContent, port, recipientPort, messageSendingType);
        System.out.println(message.toString());
        if (messageContent.equalsIgnoreCase("fim")) {
          System.out.println("encerrando cliente de " + port);
          this.sendMessage(message);
          hasConnection = Boolean.FALSE;
        } else {
          System.out.println("enviando ao servidor de " + (56001 + (port % 4)));
          this.sendMessage(message);
        }
      }
      input.close();
      output.close();
      client.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void sendMessage(final Message message) {
    try {
      Client.output.writeObject(message);
      Client.output.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
}
