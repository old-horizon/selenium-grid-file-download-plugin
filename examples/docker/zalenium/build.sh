#!/usr/bin/env bash
set -e

image=${1:-zalenium-file-download}
root=$(git rev-parse --show-toplevel)
DOCKER_BUILDKIT=1 docker build --rm -t $image -f $root/examples/docker/zalenium/Dockerfile $root
