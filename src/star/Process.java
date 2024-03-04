package star;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Process {
  private final String name;
  private final String ip;
  private final int currentPort;
  private final int nextClientPort;
  public static int PORT;

  public Process(final String name, final String ip, final int currentPort, final int nextClientPort) {
    this.name = Objects.requireNonNull(name, "O nome do processo não pode ser nulo.");
    this.ip = Objects.requireNonNull(ip, "O IP do processo não pode ser nulo.");
    this.currentPort = Objects.requireNonNull(currentPort, "A porta do processo não pode ser nulo.");
    this.nextClientPort = Objects.requireNonNull(nextClientPort, "A porta do processo não pode ser nulo.");
    PORT = this.currentPort;
    this.exec();
  }

  @SuppressWarnings("resource")
  private void exec() {
    try {
      final var serverSocket = new ServerSocket(this.currentPort);
      System.out.println("iniciando servidor " + this.currentPort);

      final var clientSocket = new Socket(this.ip, this.nextClientPort);
      final var client = new Client(clientSocket, this.name);
      final var tClient = new Thread(client);
      tClient.start();

      while (true) {
        final var clientConnectedInServer = serverSocket.accept();
        final var server = new Server(clientConnectedInServer, this.name);
        final var tServer = new Thread(server);
        tServer.start();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    new Process("P1", "127.0.0.1", 56001, 56001);
    // new Process("P2", "127.0.0.1", 56002, 56001);
    // new Process("P3", "127.0.0.1", 56003, 56001);
    // new Process("P4", "127.0.0.1", 56004, 56001);
  }
}
