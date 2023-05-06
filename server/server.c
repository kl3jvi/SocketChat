#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

#define PORT 8080
#define MAX_CONN 2
#define BUFFER_SIZE 1024

void handle_chat(int client1, int client2, char* username1, char* username2) {
    char buffer[BUFFER_SIZE];
    ssize_t msg_size;

    while (1) {
        memset(buffer, 0, BUFFER_SIZE);
        msg_size = recv(client1, buffer, BUFFER_SIZE, 0);

        if (msg_size <= 0) {
            break;
        }

        printf("%s: %s\n", username1, buffer);
        send(client2, buffer, msg_size, 0);
        memset(buffer, 0, BUFFER_SIZE);
        msg_size = recv(client2, buffer, BUFFER_SIZE, 0);

        if (msg_size <= 0) {
            break;
        }

        printf("%s: %s\n", username2, buffer);
        send(client1, buffer, msg_size, 0);
    }
}

void read_username(int socket, char* username, size_t max_length) {
    ssize_t username_size = recv(socket, username, max_length, 0);
    if (username_size <= 0) {
        perror("recv username");
        exit(EXIT_FAILURE);
    }
    username[username_size] = '\0';  // Null-terminate the username
}

int main() {
    int server_fd, new_socket1, new_socket2;
    int opt = 1;
    struct sockaddr_in address;
    int addrlen = sizeof(address);

    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("socket");
        exit(EXIT_FAILURE);
    }

    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt))) {
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }

    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEPORT, &opt, sizeof(opt))) {
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) {
        perror("bind");
        exit(EXIT_FAILURE);
    }

    if (listen(server_fd, MAX_CONN) < 0) {
        perror("listen");
        exit(EXIT_FAILURE);
    }

    printf("Started server on port %d\n", PORT);

        if ((new_socket1 = accept(server_fd, (struct sockaddr *)&address, (socklen_t *)&addrlen)) < 0) {
            perror("accept");
            exit(EXIT_FAILURE);
        }

        char username1[BUFFER_SIZE];
        read_username(new_socket1, username1, BUFFER_SIZE - 1);

        printf("Connected user 1: %s\n", username1);

        if ((new_socket2 = accept(server_fd, (struct sockaddr *)&address, (socklen_t *)&addrlen)) < 0) {
            perror("accept");
            exit(EXIT_FAILURE);
        }

        char username2[BUFFER_SIZE];
        read_username(new_socket2, username2, BUFFER_SIZE - 1);

        printf("Connected user 2: %s\n", username2);

        handle_chat(new_socket1, new_socket2, username1, username2);

    close(new_socket1);
    close(new_socket2);
    close(server_fd);
    return 0;
}