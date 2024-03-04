## Getting Started

Implementation of star and ring topology using 4 processes where each process has a thread for a client socket and a thread for a server socket.

some examples of messages:
hello -u=p3, where hello is the content of the message; -u means it is unicast and p3 is the recipient
hello everyone -b, where hello everyone is the content of the message and -b means it is broadcast

Note: the ports used internally are 56001 to 56004

## Folder Structure

The workspace contains two folders by default, where:

- `ring`: the directory with the ring topology files
- `star`: the directory with the star topology files
- `shared`: files shared between the two topologies
