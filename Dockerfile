FROM ubuntu:latest
LABEL authors="davi.carriuolo"

ENTRYPOINT ["top", "-b"]