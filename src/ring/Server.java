package ring;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class Server implements Runnable {
  private final Socket clientSocket;
  private final Client nextClient;
  private final String processName;
  private static ObjectOutputStream output;
  private static ObjectInputStream input;

  public Server(final Socket clientSocket, final String name, final Client nextClient) {
    this.clientSocket = Objects.requireNonNull(clientSocket, "O clientSocket não pode ser nulo.");
    this.processName = Objects.requireNonNull(name, "O nome do processo não pode ser nulo.");
    this.nextClient = Objects.requireNonNull(nextClient, "O nome do processo não pode ser nulo.");
  }

  @Override
  public void run() {
    final var port = clientSocket.getLocalPort();
    System.out.println("iniciando servidor " + port);
    try {
      boolean hasConnection = Boolean.TRUE;
      Message message;
      while (hasConnection) {
        input = new ObjectInputStream(this.clientSocket.getInputStream());
        output = new ObjectOutputStream(this.clientSocket.getOutputStream());
        message = (Message) input.readObject();
        // para finalizar o servidor
        if (message.getContent().equalsIgnoreCase("fim")) {
          hasConnection = Boolean.FALSE;
          continue;
        }
        // quando não para esse processo (cliente)
        if (message.getRecipient() != port &&
            message.getType().equalsIgnoreCase("u")) {
          System.out.println("encaminhando mensagem para " + port);
          nextClient.sendMessage(message);
          continue;
        }
        // quando é para esse processo (cliente)
        if (message.getRecipient() == port &&
            message.getType().equalsIgnoreCase("u")) {
          System.out.println("mensagem recebida de " + message.getSender() + ": " + message.getContent());
          continue;
        }
        if (message.getType().equalsIgnoreCase("b")) {
          System.out.println("mensagem recebida de broadcast " + message.getContent());
          if (message.getSender() != Client.PORT) {
            System.out.println("encaminhando mensagem para " + port);
            nextClient.sendMessage(message);
          }
          continue;
        }
      }
      System.out.println("encerrando servidor " + this.processName);
      input.close();
      output.close();
      clientSocket.close();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
