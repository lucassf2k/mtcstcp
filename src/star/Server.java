package star;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Server implements Runnable {
  private final Socket clientSocket;
  private final String processName;
  private Scanner scan = null;
  public static List<Socket> connections = new ArrayList<>();

  public Server(final Socket clientSocket, final String name) {
    this.clientSocket = Objects.requireNonNull(clientSocket, "O clientSocket não pode ser nulo.");
    this.processName = Objects.requireNonNull(name, "O nome do processo não pode ser nulo.");
    Server.connections.add(clientSocket);
  }

  @Override
  public void run() {
    final var port = clientSocket.getLocalPort();
    System.out.println("iniciando servidor " + port);
    try {
      boolean hasConnection = Boolean.TRUE;
      scan = new Scanner(clientSocket.getInputStream());
      Socket sender = null;
      PrintStream printer = null;
      while (hasConnection) {
        final var rawMessage = scan.nextLine();
        final var messageContent = rawMessage.split("-")[0];
        final var messageRecipient = rawMessage.split("-")[1];
        final var messageType = rawMessage.split("-")[3];
        // para finalizar o servidor
        if (messageContent.equalsIgnoreCase("fim")) {
          hasConnection = Boolean.FALSE;
          continue;
        }
        if (messageType.equalsIgnoreCase("u") &&
            Integer.valueOf(messageRecipient) == Process.PORT) {
          System.out.println("mensagem recebida de " + Process.PORT + ": " + messageContent);
          continue;
        }
        if (messageType.equalsIgnoreCase("u") &&
            Integer.valueOf(messageRecipient) != Process.PORT) {
          System.out.println("encaminhando mensagem para " + messageRecipient);
          sender = new Socket("127.0.0.1", Integer.valueOf(messageRecipient));
          printer = new PrintStream(sender.getOutputStream());
          printer.println(rawMessage);
          continue;
        }

        if (messageType.equalsIgnoreCase("b")) {
          if (Process.PORT != 56001) {
            System.out.println("mensagem de broadcast: " + messageContent);
          } else {
            for (var i = 1; i <= Server.connections.size(); i++) {
              if (Process.PORT == 56001 && i == 1) {
                System.out.println("mensagem de broadcast: " + messageContent);
              } else {
                sender = new Socket("127.0.0.1", 56000 + i);
                printer = new PrintStream(sender.getOutputStream());
                printer.println(rawMessage);
              }
            }
          }
          continue;
        }

      }
      System.out.println("encerrando servidor " + this.processName);
      scan.close();
      printer.close();
      clientSocket.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
