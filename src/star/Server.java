package star;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Server implements Runnable {
  private final Socket clientSocket;
  private final String processName;
  private Scanner scan = null;

  public Server(final Socket clientSocket, final String name, final int serverPort) {
    this.clientSocket = Objects.requireNonNull(clientSocket, "O clientSocket não pode ser nulo.");
    this.processName = Objects.requireNonNull(name, "O nome do processo não pode ser nulo.");
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
        // final var messageSender = rawMessage.split("-")[2];
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
